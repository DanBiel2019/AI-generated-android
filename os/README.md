# ai-os v0.1 — minimal bare-metal kernel for the Lenovo T410

This is a from-scratch, freestanding x86 kernel: it boots via GRUB
(Multiboot2), brings up the CPU (GDT/IDT/PIC), enumerates the PCI bus,
initializes an Intel e1000-family NIC, and runs enough of a network stack
(Ethernet, ARP, IPv4, UDP, ICMP, DHCP) to prove it can exchange real,
standard-protocol traffic over the wire. It is **not** a general-purpose OS
yet — no processes, no filesystem, no scheduler, no userland. Think of it as
milestone 1: "boot and talk on the network," which the original ask was
built around.

The "AI writes its own drivers from a vector DB and has no need for
English" framing in the original request is a separate, much bigger
research problem than boot+NIC bring-up, and this v0.1 deliberately does
not attempt it — see **Phase 2/3** below for why and how it's meant to plug
in later.

## Proof it actually works

Built and boot-tested in this session under QEMU (`qemu-system-x86_64`,
emulating an 82540EM e1000 NIC + slirp usermode networking). Serial console
output from an actual run:

```
ai-os v0.1 booting...
gdt: loaded flat descriptor table
idt: exception handlers installed, PIC remapped and masked (polling mode)
PCI: scanning config space...
  0:3.0  vendor=0x8086 device=0x100e class=0x2 sub=0x0 progif=0x0
PCI: 6 function(s) found.
e1000: found device 0x100e at PCI 0:3.0
e1000: MMIO base = 0xfeb80000
nic: MAC address = 52:54:00:12:34:56
dhcp: sending DISCOVER (xid=0x1a0b0c0d)
entering poll loop (eth/arp/ip/icmp/dhcp)...
dhcp: OFFER received, yiaddr=10.0.2.15 netmask=255.255.255.0 gateway=10.0.2.2 dns=10.0.2.3
dhcp: interface configured, address=10.0.2.15 (offer applied without REQUEST/ACK -- see dhcp.c)
```

A packet capture of the same run (`tcpdump -e -nn`) confirms the frames on
the wire are well-formed standard DHCP/BOOTP:

```
52:54:00:12:34:56 > ff:ff:ff:ff:ff:ff, IPv4: 0.0.0.0.68 > 255.255.255.255.67: BOOTP/DHCP, Request from 52:54:00:12:34:56, length 249
52:55:0a:00:02:02 > ff:ff:ff:ff:ff:ff, IPv4: 10.0.2.2.67 > 255.255.255.255.68: BOOTP/DHCP, Reply, length 548
```

That's boot, PCI enumeration, NIC bring-up, and a real Ethernet/IP/UDP/DHCP
round trip, all working end to end.

## Building and running

Requires `gcc` (with 32-bit multilib), `nasm`, `ld`, `grub-mkrescue`,
`xorriso`, and `qemu-system-x86_64`. On Debian/Ubuntu:

```
apt-get install -y qemu-system-x86 grub-pc-bin grub-common xorriso nasm mtools
```

```
cd os
make        # builds build/kernel.elf
make iso    # wraps it in a GRUB-bootable ISO at build/ai-os.iso
make run    # boots it in QEMU with an e1000 NIC and serial console on stdio
```

## Layout

```
os/
  boot/boot.asm        Multiboot2 header + entry point, hands off to kernel_main
  linker.ld             Link script, loads at 1 MiB
  iso/boot/grub/grub.cfg
  kernel/
    kernel.c            Entry point: init sequence + poll loop
    gdt.c/idt.c/pic.c    CPU/interrupt controller setup (exceptions handled; IRQs
                         masked -- this driver polls the NIC rather than using IRQs)
    serial.c/vga.c/kprintf.c   Debug output (COM1 + VGA text mode)
    pci.c                Config-space bus scan, device lookup by vendor/device ID
    e1000.c              NIC driver: reset, RX/TX descriptor rings, polling send/receive
    net/                 eth.c, arp.c, ip.c, icmp.c, udp.c, dhcp.c
```

## What's implemented vs. simplified

- **No paging, no heap.** Everything is statically allocated; the kernel
  runs with paging disabled so linear == physical, which is what makes DMA
  buffer addresses trivial to compute. Fine for this scope; a real memory
  manager is Phase 2+ work.
- **Polling, not interrupt-driven.** IRQs are masked at the PIC; the main
  loop just calls `eth_poll()` continuously. Simpler and more robust for a
  first cut. Moving RX to an IRQ handler is a natural next step.
- **No ARP cache / routing table.** `ip_send()` requires the caller to
  already know the destination MAC — either the Ethernet broadcast address
  (used for DHCP DISCOVER) or the source MAC of a frame being replied to
  (used for ICMP echo replies, ARP replies). This works for "answer whoever
  contacted us" but not for initiating a connection to an arbitrary unicast
  host yet.
- **DHCP client stops at OFFER.** A conformant client does
  DISCOVER → OFFER → REQUEST → ACK; this one applies the OFFER directly
  without the handshake. That's fine for proving the round trip against
  QEMU's DHCP server; don't point it at a real network with other DHCP
  clients until REQUEST/ACK is added.
- **One bug worth knowing about if you extend this**: the RX/TX descriptor
  rings are declared `volatile` on purpose. Without it, the compiler
  correctly-per-the-language-rules assumes nothing external changes
  `desc->status`, and at `-O2` it hoisted the completion-wait loop in
  `e1000_send()` into an infinite spin — the frame had already gone out on
  the wire (confirmed via packet capture) but the driver never noticed.
  Any register or memory location a device writes to via DMA needs the same
  treatment.

## Porting to the real T410

The T410's onboard NIC is an Intel 82577LM (PCI ID `8086:10EA`), which is
already in this driver's supported-device list, and it shares the same core
register map (CTRL/STATUS/RCTL/TCTL/RDBAL/TDBAL/...) as the 82540EM this was
tested against — so PCI enumeration, reset, and RX/TX ring setup should
carry over directly. Two things are known gaps, both flagged in
`e1000.c`'s header comment:

1. **NVM/EEPROM access.** This driver reads the MAC address from the
   RAL0/RAH0 shadow registers (which hardware loads from NVM at power-on),
   sidestepping EEPROM protocol differences entirely. That should work
   as-is on real hardware. If you need to *write* NVM later, the 82577
   uses a different access protocol than the classic EERD bit layout used
   by older e1000 chips — consult the Linux `e1000e` driver
   (`drivers/net/ethernet/intel/e1000e/nvm.c`) as the reference.
2. **PHY power-up / link training.** Real copper PHYs sometimes need more
   than `CTRL.SLU` to bring the link up reliably (power management state,
   auto-negotiation restart). If link status reads down on real hardware
   after the sequence that works in QEMU, this is the first place to look
   — again, `e1000e`'s `phy.c` is the reference implementation.

The T410 boots via legacy BIOS (no UEFI), which is exactly what this
Multiboot2/GRUB setup targets, so no bootloader changes should be needed.
Recommended bring-up path: write `build/ai-os.iso` to a USB stick
(`dd if=build/ai-os.iso of=/dev/sdX`), boot the T410 from it, and watch
COM1 (real serial port or a USB-serial adapter) at 38400 baud 8N1 for the
same log lines shown above. **Don't touch the internal drive until you've
confirmed boot + NIC bring-up from USB.**

## Phase 2: the self-updating loop

The original idea was an AI that writes its own updates, tests them in a
clone of itself, and commits on success. A booting kernel has no libc, no
Python, no vector database — so that loop has to live *outside* the kernel,
as tooling that treats this repo as its work product:

```
   edit kernel source
          |
          v
   make iso                          (this repo's build)
          |
          v
   boot build/ai-os.iso in QEMU      ("a clone of itself")
          |
          v
   automated test harness:
     - grep serial log for expected milestones
       (PCI device found, MAC printed, DHCP OFFER received, no CPU exception)
     - optionally script `tcpdump`/pcap assertions like the ones used above
          |
          v
   pass -> git commit ; fail -> discard and retry
```

Everything needed for that loop already exists and was exercised manually
in this session: the Makefile, the QEMU boot command, and serial output
that's `grep`-able for well-defined milestone strings. Turning it into an
actual autonomous loop is scripting work on top of what's here, not a
kernel change.

## Phase 3: where a vector DB would actually fit

Not inside the kernel — inside the Phase 2 tooling, as retrieval context
for whatever model is generating the driver/kernel changes: register maps,
datasheet excerpts, past working (and broken-then-fixed) driver revisions,
errata like the `volatile` bug above. That's a reasonable, well-trodden use
of embeddings/RAG. It doesn't need to run on the T410 itself, and definitely
doesn't need to be booted before the kernel can enumerate PCI or bring up
Ethernet — decoupling it from the boot path (as this v0.1 does) is what
keeps "does it boot" and "can the AI improve it" as separable, independently
testable problems.
