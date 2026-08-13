#ifndef NET_IP_H
#define NET_IP_H

#include <stdint.h>

#define IP_PROTO_ICMP 1
#define IP_PROTO_UDP  17

struct ipv4_hdr {
    uint8_t  ver_ihl;
    uint8_t  tos;
    uint16_t total_len;
    uint16_t id;
    uint16_t flags_frag;
    uint8_t  ttl;
    uint8_t  protocol;
    uint16_t checksum;
    uint32_t src; /* network byte order */
    uint32_t dst; /* network byte order */
} __attribute__((packed));

uint16_t ip_checksum(const void *data, uint16_t len);

/*
 * Builds and sends an IPv4 packet. Since this v0.1 stack has no ARP cache /
 * routing table, the caller must already know the destination MAC -- either
 * the Ethernet broadcast address (for broadcast sends like DHCP DISCOVER)
 * or the source MAC of a frame we're replying to (turn-around, used for
 * ICMP echo replies).
 */
void ip_send(const uint8_t dst_mac[6], uint32_t dst_ip, uint8_t protocol,
             const void *payload, uint16_t len);

void ip_handle_packet(const uint8_t src_mac[6], const uint8_t *data, uint16_t len);

#endif
