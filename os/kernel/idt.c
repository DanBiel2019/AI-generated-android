#include "idt.h"
#include "kprintf.h"
#include <stdint.h>

struct idt_entry {
    uint16_t base_low;
    uint16_t sel;
    uint8_t  always0;
    uint8_t  flags;
    uint16_t base_high;
} __attribute__((packed));

struct idt_ptr {
    uint16_t limit;
    uint32_t base;
} __attribute__((packed));

static struct idt_entry idt[256];
static struct idt_ptr ip;

extern void idt_load(uint32_t);
extern void *isr_stub_table[32];

static void idt_set_gate(int n, uint32_t handler) {
    idt[n].base_low  = handler & 0xFFFF;
    idt[n].base_high = (handler >> 16) & 0xFFFF;
    idt[n].sel       = 0x08;
    idt[n].always0   = 0;
    idt[n].flags     = 0x8E; /* present, ring0, 32-bit interrupt gate */
}

static const char *exception_name(int n) {
    static const char *names[] = {
        "Divide-by-zero", "Debug", "NMI", "Breakpoint", "Overflow",
        "Bound Range", "Invalid Opcode", "Device Not Available",
        "Double Fault", "Coprocessor Overrun", "Invalid TSS",
        "Segment Not Present", "Stack-Segment Fault", "General Protection Fault",
        "Page Fault", "Reserved", "x87 FP Exception", "Alignment Check",
        "Machine Check", "SIMD FP Exception"
    };
    if (n >= 0 && n < (int)(sizeof(names) / sizeof(names[0]))) return names[n];
    return "Unknown";
}

void isr_handler(uint32_t int_no, uint32_t err_code) {
    kprintf("\n*** CPU EXCEPTION %u: %s (err=0x%x) ***\n", int_no, exception_name((int)int_no), err_code);
    kprintf("System halted.\n");
    for (;;) { __asm__ volatile ("cli; hlt"); }
}

void idt_init(void) {
    ip.limit = sizeof(idt) - 1;
    ip.base  = (uint32_t)&idt;

    for (int i = 0; i < 256; i++) idt_set_gate(i, 0);
    for (int i = 0; i < 32; i++) idt_set_gate(i, (uint32_t)isr_stub_table[i]);

    idt_load((uint32_t)&ip);
}
