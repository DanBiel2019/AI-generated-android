#ifndef PCI_H
#define PCI_H

#include <stdint.h>

typedef struct {
    uint8_t  bus, device, function;
    uint16_t vendor_id, device_id;
    uint8_t  class_code, subclass, prog_if;
} pci_device_t;

uint32_t pci_config_read32(uint8_t bus, uint8_t device, uint8_t function, uint8_t offset);
void     pci_config_write32(uint8_t bus, uint8_t device, uint8_t function, uint8_t offset, uint32_t value);

/* Scans every bus/device/function and logs what it finds via kprintf. */
void pci_scan_and_print(void);

/*
 * Looks for the first PCI function whose vendor is Intel (0x8086) and whose
 * device ID is in `ids` (length `n`). Returns 1 and fills `out` on success.
 */
int pci_find_by_ids(uint16_t vendor, const uint16_t *ids, int n, pci_device_t *out);

#endif
