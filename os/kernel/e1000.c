/*
 * Minimal polling-mode driver for the Intel e1000 register family.
 *
 * Validated against QEMU's emulated 82540EM (PCI id 8086:100E, QEMU's
 * default "-device e1000"). The core register map (CTRL/STATUS/RCTL/TCTL/
 * RDBAL/TDBAL/...) is shared across the whole e1000/e1000e family, so this
 * also matches the T410's onboard 82577LM (8086:10EA) for reset, ring setup
 * and TX/RX. Real 82577LM hardware differs in two places this driver does
 * NOT yet handle: NVM/EEPROM access uses a different protocol than the
 * classic EERD bit layout, and the copper PHY needs an explicit power-up /
 * link-training sequence beyond CTRL.SLU. MAC address here is read back
 * from the RAL0/RAH0 shadow registers (loaded from NVM by hardware at
 * power-on) instead of walking the EEPROM protocol, which sidesteps the
 * first issue. See os/README.md for the real-hardware bring-up plan.
 */

#include "e1000.h"
#include "pci.h"
#include "io.h"
#include "string.h"
#include "kprintf.h"

/* Register offsets */
#define REG_CTRL        0x0000
#define REG_STATUS      0x0008
#define REG_CTRL_EXT    0x0018
#define REG_ICR         0x00C0
#define REG_IMS         0x00D0
#define REG_IMC         0x00D8
#define REG_RCTL        0x0100
#define REG_TCTL        0x0400
#define REG_TIPG        0x0410
#define REG_RDBAL       0x2800
#define REG_RDBAH       0x2804
#define REG_RDLEN       0x2808
#define REG_RDH         0x2810
#define REG_RDT         0x2818
#define REG_TDBAL       0x3800
#define REG_TDBAH       0x3804
#define REG_TDLEN       0x3808
#define REG_TDH         0x3810
#define REG_TDT         0x3818
#define REG_RAL0        0x5400
#define REG_RAH0        0x5404
#define REG_MTA         0x5200

#define CTRL_RST   (1u << 26)
#define CTRL_SLU   (1u << 6)
#define CTRL_ASDE  (1u << 5)

#define RCTL_EN    (1u << 1)
#define RCTL_SBP   (1u << 2)
#define RCTL_UPE   (1u << 3)
#define RCTL_MPE   (1u << 4)
#define RCTL_BAM   (1u << 15)
#define RCTL_BSIZE_2048 (0u << 16)
#define RCTL_SECRC (1u << 26)

#define TCTL_EN    (1u << 1)
#define TCTL_PSP   (1u << 3)
#define TCTL_CT_SHIFT   4
#define TCTL_COLD_SHIFT 12

#define TXD_CMD_EOP  0x01
#define TXD_CMD_IFCS 0x02
#define TXD_CMD_RS   0x08
#define TXD_STAT_DD  0x01

#define RXD_STAT_DD  0x01
#define RXD_STAT_EOP 0x02

#define RX_DESC_COUNT 32
#define TX_DESC_COUNT 32
#define RX_BUF_SIZE   2048

struct e1000_rx_desc {
    uint64_t addr;
    uint16_t length;
    uint16_t checksum;
    uint8_t  status;
    uint8_t  errors;
    uint16_t special;
} __attribute__((packed));

struct e1000_tx_desc {
    uint64_t addr;
    uint16_t length;
    uint8_t  cso;
    uint8_t  cmd;
    uint8_t  status;
    uint8_t  css;
    uint16_t special;
} __attribute__((packed));

static volatile uint8_t *mmio_base;

/*
 * volatile because the NIC writes `status` back via DMA whenever it likes;
 * without it the compiler treats descriptor fields as loop-invariant and
 * hoists/eliminates the polling reads below (seen in practice: e1000_send's
 * completion wait spun forever at -O2 even though the frame had already
 * gone out on the wire).
 */
static volatile struct e1000_rx_desc rx_ring[RX_DESC_COUNT] __attribute__((aligned(16)));
static volatile struct e1000_tx_desc tx_ring[TX_DESC_COUNT] __attribute__((aligned(16)));
static uint8_t rx_buffers[RX_DESC_COUNT][RX_BUF_SIZE] __attribute__((aligned(16)));
static uint8_t tx_buffers[TX_DESC_COUNT][RX_BUF_SIZE] __attribute__((aligned(16)));

static uint16_t rx_tail;
static uint16_t tx_tail;

static const uint16_t SUPPORTED_DEVICE_IDS[] = {
    0x100E, /* 82540EM -- QEMU's default "-device e1000" */
    0x100F, /* 82545EM */
    0x1004, /* 82543GC */
    0x10D3, /* 82574L  -- QEMU's "-device e1000e" */
    0x10EA, /* 82577LM -- Lenovo T410 onboard NIC */
    0x10EB, /* 82577LM variant */
};

static inline void mmio_write32(uint32_t reg, uint32_t value) {
    *(volatile uint32_t *)(mmio_base + reg) = value;
}

static inline uint32_t mmio_read32(uint32_t reg) {
    return *(volatile uint32_t *)(mmio_base + reg);
}

static void e1000_setup_rx(void) {
    memset((void *)rx_ring, 0, sizeof(rx_ring));
    for (int i = 0; i < RX_DESC_COUNT; i++) {
        rx_ring[i].addr = (uint64_t)(uintptr_t)&rx_buffers[i][0];
    }

    uint32_t base = (uint32_t)(uintptr_t)&rx_ring[0];
    mmio_write32(REG_RDBAL, base);
    mmio_write32(REG_RDBAH, 0);
    mmio_write32(REG_RDLEN, RX_DESC_COUNT * sizeof(struct e1000_rx_desc));
    mmio_write32(REG_RDH, 0);
    mmio_write32(REG_RDT, RX_DESC_COUNT - 1);
    rx_tail = RX_DESC_COUNT - 1;

    mmio_write32(REG_RCTL, RCTL_EN | RCTL_SBP | RCTL_UPE | RCTL_MPE |
                            RCTL_BAM | RCTL_BSIZE_2048 | RCTL_SECRC);
}

static void e1000_setup_tx(void) {
    memset((void *)tx_ring, 0, sizeof(tx_ring));
    for (int i = 0; i < TX_DESC_COUNT; i++) {
        tx_ring[i].addr = (uint64_t)(uintptr_t)&tx_buffers[i][0];
        tx_ring[i].status = TXD_STAT_DD;
    }

    uint32_t base = (uint32_t)(uintptr_t)&tx_ring[0];
    mmio_write32(REG_TDBAL, base);
    mmio_write32(REG_TDBAH, 0);
    mmio_write32(REG_TDLEN, TX_DESC_COUNT * sizeof(struct e1000_tx_desc));
    mmio_write32(REG_TDH, 0);
    mmio_write32(REG_TDT, 0);
    tx_tail = 0;

    mmio_write32(REG_TIPG, 0x0060200A); /* standard 802.3 timings */
    mmio_write32(REG_TCTL, TCTL_EN | TCTL_PSP |
                            (15u << TCTL_CT_SHIFT) |
                            (64u << TCTL_COLD_SHIFT));
}

int e1000_init(uint8_t mac_out[6]) {
    pci_device_t dev;
    int n = (int)(sizeof(SUPPORTED_DEVICE_IDS) / sizeof(SUPPORTED_DEVICE_IDS[0]));
    if (!pci_find_by_ids(0x8086, SUPPORTED_DEVICE_IDS, n, &dev)) {
        kprintf("e1000: no supported NIC found on the PCI bus\n");
        return 0;
    }

    kprintf("e1000: found device 0x%x at PCI %u:%u.%u\n",
            dev.device_id, dev.bus, dev.device, dev.function);

    /* Enable memory space + bus mastering. */
    uint32_t cmd = pci_config_read32(dev.bus, dev.device, dev.function, 0x04);
    cmd |= (1u << 1) | (1u << 2);
    pci_config_write32(dev.bus, dev.device, dev.function, 0x04, cmd);

    uint32_t bar0 = pci_config_read32(dev.bus, dev.device, dev.function, 0x10);
    uint32_t phys_base = bar0 & 0xFFFFFFF0u;
    mmio_base = (volatile uint8_t *)(uintptr_t)phys_base;
    kprintf("e1000: MMIO base = 0x%x\n", phys_base);

    /* Reset the controller and wait for it to come back. */
    mmio_write32(REG_CTRL, mmio_read32(REG_CTRL) | CTRL_RST);
    for (volatile int i = 0; i < 1000000; i++) { }

    mmio_write32(REG_IMC, 0xFFFFFFFF); /* mask all interrupts, we poll */
    mmio_write32(REG_CTRL, mmio_read32(REG_CTRL) | CTRL_SLU | CTRL_ASDE);

    /* Clear the multicast table. */
    for (int i = 0; i < 128; i++) mmio_write32(REG_MTA + i * 4, 0);

    /* MAC address: hardware loads RAL0/RAH0 from NVM at power-on. */
    uint32_t ral = mmio_read32(REG_RAL0);
    uint32_t rah = mmio_read32(REG_RAH0);
    mac_out[0] = ral & 0xFF;
    mac_out[1] = (ral >> 8) & 0xFF;
    mac_out[2] = (ral >> 16) & 0xFF;
    mac_out[3] = (ral >> 24) & 0xFF;
    mac_out[4] = rah & 0xFF;
    mac_out[5] = (rah >> 8) & 0xFF;

    e1000_setup_rx();
    e1000_setup_tx();

    kprintf("e1000: link status register = 0x%x\n", mmio_read32(REG_STATUS));
    return 1;
}

void e1000_send(const void *data, uint16_t len) {
    if (len > RX_BUF_SIZE) len = RX_BUF_SIZE;

    memcpy(tx_buffers[tx_tail], data, len);
    tx_ring[tx_tail].length = len;
    tx_ring[tx_tail].cmd = TXD_CMD_EOP | TXD_CMD_IFCS | TXD_CMD_RS;
    tx_ring[tx_tail].status = 0;

    uint16_t old_tail = tx_tail;
    tx_tail = (tx_tail + 1) % TX_DESC_COUNT;
    mmio_write32(REG_TDT, tx_tail);

    while (!(tx_ring[old_tail].status & TXD_STAT_DD)) { }
}

uint16_t e1000_poll_receive(void *buf, uint16_t maxlen) {
    uint16_t next = (rx_tail + 1) % RX_DESC_COUNT;
    volatile struct e1000_rx_desc *desc = &rx_ring[next];

    if (!(desc->status & RXD_STAT_DD)) return 0;

    uint16_t len = desc->length;
    if (len > maxlen) len = maxlen;
    memcpy(buf, rx_buffers[next], len);

    desc->status = 0;
    mmio_write32(REG_RDT, next);
    rx_tail = next;

    return len;
}
