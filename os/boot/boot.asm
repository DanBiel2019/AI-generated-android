; Multiboot2 entry point.
; GRUB hands us 32-bit protected mode, paging disabled, interrupts disabled.
; We set up a stack and jump straight into C.

MB2_MAGIC       equ 0xE85250D6
MB2_ARCH_I386   equ 0
MB2_HDR_LEN     equ (multiboot_header_end - multiboot_header)
MB2_CHECKSUM    equ -(MB2_MAGIC + MB2_ARCH_I386 + MB2_HDR_LEN)

section .multiboot
align 8
multiboot_header:
    dd MB2_MAGIC
    dd MB2_ARCH_I386
    dd MB2_HDR_LEN
    dd MB2_CHECKSUM
    ; end tag
    dw 0    ; type
    dw 0    ; flags
    dd 8    ; size
multiboot_header_end:

section .bss
align 16
stack_bottom:
    resb 16384          ; 16 KiB kernel stack
stack_top:

section .text
global _start
extern kernel_main

_start:
    mov esp, stack_top
    mov ebp, esp

    ; EAX = multiboot2 magic, EBX = pointer to boot info; pass through to C
    push ebx
    push eax
    call kernel_main

    cli
.hang:
    hlt
    jmp .hang
