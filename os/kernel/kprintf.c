#include "kprintf.h"
#include "serial.h"
#include "vga.h"
#include <stdarg.h>
#include <stdint.h>

static void putc_both(char c) {
    serial_putc(c);
    vga_putc(c);
}

static void print_uint(uint32_t v, unsigned base, int upper) {
    char buf[32];
    const char *digits = upper ? "0123456789ABCDEF" : "0123456789abcdef";
    int i = 0;
    if (v == 0) {
        putc_both('0');
        return;
    }
    while (v > 0) {
        buf[i++] = digits[v % base];
        v /= base;
    }
    while (i > 0) putc_both(buf[--i]);
}

static void print_int(int32_t v) {
    if (v < 0) {
        putc_both('-');
        print_uint((uint32_t)(-v), 10, 0);
    } else {
        print_uint((uint32_t)v, 10, 0);
    }
}

void kprintf(const char *fmt, ...) {
    va_list ap;
    va_start(ap, fmt);

    for (const char *p = fmt; *p; p++) {
        if (*p != '%') {
            putc_both(*p);
            continue;
        }
        p++;
        switch (*p) {
            case 's': {
                const char *s = va_arg(ap, const char *);
                if (!s) s = "(null)";
                while (*s) putc_both(*s++);
                break;
            }
            case 'd': print_int(va_arg(ap, int32_t)); break;
            case 'u': print_uint(va_arg(ap, uint32_t), 10, 0); break;
            case 'x': print_uint(va_arg(ap, uint32_t), 16, 0); break;
            case 'X': print_uint(va_arg(ap, uint32_t), 16, 1); break;
            case 'c': putc_both((char)va_arg(ap, int)); break;
            case '%': putc_both('%'); break;
            case 0: goto done;
            default: putc_both('%'); putc_both(*p); break;
        }
    }
done:
    va_end(ap);
}
