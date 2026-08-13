CC      = gcc
AS      = nasm
LD      = ld

CFLAGS  = -m32 -std=gnu11 -ffreestanding -fno-stack-protector -fno-pic -fno-pie \
          -nostdlib -Wall -Wextra -O2 -Ikernel
ASFLAGS = -f elf32
LDFLAGS = -m elf_i386 -T linker.ld -nostdlib

BUILD   = build
ISO_DIR = iso

C_SRCS  = $(shell find kernel -name '*.c')
ASM_SRCS = boot/boot.asm kernel/gdt_flush.asm kernel/idt_load.asm kernel/isr.asm

C_OBJS  = $(patsubst %.c,$(BUILD)/%.o,$(C_SRCS))
ASM_OBJS = $(patsubst %.asm,$(BUILD)/%.o,$(ASM_SRCS))
OBJS    = $(ASM_OBJS) $(C_OBJS)

KERNEL  = $(BUILD)/kernel.elf
ISO     = $(BUILD)/ai-os.iso

.PHONY: all clean run iso

all: $(KERNEL)

$(BUILD)/%.o: %.c
	@mkdir -p $(dir $@)
	$(CC) $(CFLAGS) -c $< -o $@

$(BUILD)/%.o: %.asm
	@mkdir -p $(dir $@)
	$(AS) $(ASFLAGS) $< -o $@

$(KERNEL): $(OBJS) linker.ld
	$(LD) $(LDFLAGS) -o $@ $(OBJS)

iso: $(KERNEL)
	@mkdir -p $(ISO_DIR)/boot/grub
	cp $(KERNEL) $(ISO_DIR)/boot/kernel.elf
	grub-mkrescue -o $(ISO) $(ISO_DIR) 2>/dev/null

run: iso
	qemu-system-x86_64 \
		-cdrom $(ISO) \
		-m 256M \
		-netdev user,id=n0 \
		-device e1000,netdev=n0 \
		-serial stdio \
		-display none \
		-no-reboot

clean:
	rm -rf $(BUILD)
