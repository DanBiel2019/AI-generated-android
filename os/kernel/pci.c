#include "pci.h"
#include "io.h"
#include "kprintf.h"

#define PCI_CONFIG_ADDRESS 0xCF8
#define PCI_CONFIG_DATA    0xCFC

static uint32_t pci_address(uint8_t bus, uint8_t device, uint8_t function, uint8_t offset) {
    return 0x80000000u
         | ((uint32_t)bus << 16)
         | ((uint32_t)(device & 0x1F) << 11)
         | ((uint32_t)(function & 0x07) << 8)
         | (offset & 0xFC);
}

uint32_t pci_config_read32(uint8_t bus, uint8_t device, uint8_t function, uint8_t offset) {
    outl(PCI_CONFIG_ADDRESS, pci_address(bus, device, function, offset));
    return inl(PCI_CONFIG_DATA);
}

void pci_config_write32(uint8_t bus, uint8_t device, uint8_t function, uint8_t offset, uint32_t value) {
    outl(PCI_CONFIG_ADDRESS, pci_address(bus, device, function, offset));
    outl(PCI_CONFIG_DATA, value);
}

static int probe(uint8_t bus, uint8_t device, uint8_t function, pci_device_t *out) {
    uint32_t id = pci_config_read32(bus, device, function, 0x00);
    uint16_t vendor = id & 0xFFFF;
    if (vendor == 0xFFFF) return 0;

    uint32_t class_reg = pci_config_read32(bus, device, function, 0x08);

    out->bus = bus;
    out->device = device;
    out->function = function;
    out->vendor_id = vendor;
    out->device_id = (id >> 16) & 0xFFFF;
    out->prog_if    = (class_reg >> 8) & 0xFF;
    out->subclass   = (class_reg >> 16) & 0xFF;
    out->class_code = (class_reg >> 24) & 0xFF;
    return 1;
}

void pci_scan_and_print(void) {
    kprintf("PCI: scanning config space...\n");
    int found = 0;
    for (uint32_t bus = 0; bus < 256; bus++) {
        for (uint32_t device = 0; device < 32; device++) {
            for (uint32_t function = 0; function < 8; function++) {
                pci_device_t d;
                if (!probe((uint8_t)bus, (uint8_t)device, (uint8_t)function, &d)) {
                    if (function == 0) break; /* no function 0 => nothing at this device */
                    continue;
                }
                found++;
                kprintf("  %u:%u.%u  vendor=0x%x device=0x%x class=0x%x sub=0x%x progif=0x%x\n",
                        bus, device, function, d.vendor_id, d.device_id,
                        d.class_code, d.subclass, d.prog_if);

                if (function == 0) {
                    uint32_t header = pci_config_read32((uint8_t)bus, (uint8_t)device, 0, 0x0C);
                    if (!((header >> 16) & 0x80)) break; /* not multi-function */
                }
            }
        }
    }
    kprintf("PCI: %d function(s) found.\n", found);
}

int pci_find_by_ids(uint16_t vendor, const uint16_t *ids, int n, pci_device_t *out) {
    for (uint32_t bus = 0; bus < 256; bus++) {
        for (uint32_t device = 0; device < 32; device++) {
            for (uint32_t function = 0; function < 8; function++) {
                pci_device_t d;
                if (!probe((uint8_t)bus, (uint8_t)device, (uint8_t)function, &d)) {
                    if (function == 0) break;
                    continue;
                }
                if (d.vendor_id == vendor) {
                    for (int i = 0; i < n; i++) {
                        if (d.device_id == ids[i]) {
                            *out = d;
                            return 1;
                        }
                    }
                }
                if (function == 0) {
                    uint32_t header = pci_config_read32((uint8_t)bus, (uint8_t)device, 0, 0x0C);
                    if (!((header >> 16) & 0x80)) break;
                }
            }
        }
    }
    return 0;
}
