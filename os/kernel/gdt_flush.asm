global gdt_flush
gdt_flush:
    mov eax, [esp + 4]
    lgdt [eax]

    mov ax, 0x10        ; data segment selector (index 2 << 3)
    mov ds, ax
    mov es, ax
    mov fs, ax
    mov gs, ax
    mov ss, ax

    jmp 0x08:.flush      ; code segment selector (index 1 << 3)
.flush:
    ret
