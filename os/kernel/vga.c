#include "vga.h"
#include <stdint.h>

#define VGA_MEM   ((volatile uint16_t *)0xB8000)
#define VGA_COLS  80
#define VGA_ROWS  25
#define VGA_ATTR  0x0F  /* white on black */

static int cursor_row = 0;
static int cursor_col = 0;

static void vga_scroll(void) {
    for (int r = 1; r < VGA_ROWS; r++) {
        for (int c = 0; c < VGA_COLS; c++) {
            VGA_MEM[(r - 1) * VGA_COLS + c] = VGA_MEM[r * VGA_COLS + c];
        }
    }
    for (int c = 0; c < VGA_COLS; c++) {
        VGA_MEM[(VGA_ROWS - 1) * VGA_COLS + c] = (VGA_ATTR << 8) | ' ';
    }
    cursor_row = VGA_ROWS - 1;
}

void vga_init(void) {
    for (int i = 0; i < VGA_COLS * VGA_ROWS; i++) {
        VGA_MEM[i] = (VGA_ATTR << 8) | ' ';
    }
    cursor_row = 0;
    cursor_col = 0;
}

void vga_putc(char c) {
    if (c == '\n') {
        cursor_col = 0;
        cursor_row++;
    } else {
        VGA_MEM[cursor_row * VGA_COLS + cursor_col] = (VGA_ATTR << 8) | (uint8_t)c;
        cursor_col++;
        if (cursor_col >= VGA_COLS) {
            cursor_col = 0;
            cursor_row++;
        }
    }
    if (cursor_row >= VGA_ROWS) vga_scroll();
}

void vga_write(const char *s) {
    while (*s) vga_putc(*s++);
}
