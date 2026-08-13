#include "pic.h"
#include "io.h"

#define PIC1 0x20
#define PIC2 0xA0
#define PIC1_CMD PIC1
#define PIC1_DATA (PIC1 + 1)
#define PIC2_CMD PIC2
#define PIC2_DATA (PIC2 + 1)

#define ICW1_INIT 0x10
#define ICW1_ICW4 0x01
#define ICW4_8086 0x01

/*
 * Remap the legacy 8259 PICs so IRQ0-15 land on vectors 32-47,
 * clear of the CPU exception vectors 0-31, then mask every line.
 * We drive the NIC by polling for this first cut, so no IRQs are
 * unmasked yet -- this just gets them out of the way safely.
 */
void pic_remap_and_mask_all(void) {
    outb(PIC1_CMD, ICW1_INIT | ICW1_ICW4);
    io_wait();
    outb(PIC2_CMD, ICW1_INIT | ICW1_ICW4);
    io_wait();

    outb(PIC1_DATA, 32);      /* master offset -> 32 */
    io_wait();
    outb(PIC2_DATA, 40);      /* slave offset -> 40 */
    io_wait();

    outb(PIC1_DATA, 4);       /* tell master about slave at IRQ2 */
    io_wait();
    outb(PIC2_DATA, 2);       /* tell slave its cascade identity */
    io_wait();

    outb(PIC1_DATA, ICW4_8086);
    io_wait();
    outb(PIC2_DATA, ICW4_8086);
    io_wait();

    outb(PIC1_DATA, 0xFF);    /* mask all master lines */
    outb(PIC2_DATA, 0xFF);    /* mask all slave lines */
}
