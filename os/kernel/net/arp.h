#ifndef NET_ARP_H
#define NET_ARP_H

#include <stdint.h>

struct arp_packet {
    uint16_t htype;
    uint16_t ptype;
    uint8_t  hlen;
    uint8_t  plen;
    uint16_t oper;
    uint8_t  sha[6];
    uint32_t spa; /* network byte order */
    uint8_t  tha[6];
    uint32_t tpa; /* network byte order */
} __attribute__((packed));

void arp_handle_packet(const uint8_t *data, uint16_t len);

#endif
