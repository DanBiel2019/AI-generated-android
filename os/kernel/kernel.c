#include <stdint.h>
#include "serial.h"
#include "vga.h"
#include "kprintf.h"
#include "gdt.h"
#include "idt.h"
#include "pic.h"
#include "pci.h"
#include "e1000.h"
#include "net/net.h"
#include "net/eth.h"
#include "net/dhcp.h"

#define MULTIBOOT2_MAGIC 0x36D76289u

void kernel_main(uint32_t magic, uint32_t mb_info) {
    (void)mb_info;

    serial_init();
    vga_init();

    kprintf("ai-os v0.1 booting...\n");
    if (magic != MULTIBOOT2_MAGIC) {
        kprintf("warning: unexpected boot magic 0x%x (expected multiboot2)\n", magic);
    }

    gdt_init();
    kprintf("gdt: loaded flat descriptor table\n");

    idt_init();
    pic_remap_and_mask_all();
    kprintf("idt: exception handlers installed, PIC remapped and masked (polling mode)\n");

    pci_scan_and_print();

    if (!e1000_init(g_mac)) {
        kprintf("FATAL: no NIC found, halting.\n");
        for (;;) { __asm__ volatile ("cli; hlt"); }
    }

    kprintf("nic: MAC address = ");
    net_print_mac(g_mac);
    kprintf("\n");

    dhcp_send_discover();

    kprintf("entering poll loop (eth/arp/ip/icmp/dhcp)...\n");
    uint32_t last_retry = 0;
    uint32_t tick = 0;
    for (;;) {
        eth_poll();

        if (g_ip == 0) {
            tick++;
            if (tick - last_retry > 20000000u) {
                last_retry = tick;
                dhcp_send_discover();
            }
        }
    }
}
