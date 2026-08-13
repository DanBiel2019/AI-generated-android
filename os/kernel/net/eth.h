#ifndef NET_ETH_H
#define NET_ETH_H

#include <stdint.h>

#define ETHERTYPE_IPV4 0x0800
#define ETHERTYPE_ARP  0x0806

struct eth_hdr {
    uint8_t  dst[6];
    uint8_t  src[6];
    uint16_t ethertype; /* network byte order */
} __attribute__((packed));

/* Builds and transmits one Ethernet frame. `payload`/`len` is everything after the header. */
void eth_send(const uint8_t dst_mac[6], uint16_t ethertype, const void *payload, uint16_t len);

/* Polls the NIC once; if a frame is waiting, parses and dispatches it. */
void eth_poll(void);

#endif
