#ifndef NET_UDP_H
#define NET_UDP_H

#include <stdint.h>

struct udp_hdr {
    uint16_t src_port;
    uint16_t dst_port;
    uint16_t length;
    uint16_t checksum;
} __attribute__((packed));

void udp_send(const uint8_t dst_mac[6], uint32_t dst_ip, uint16_t src_port, uint16_t dst_port,
              const void *payload, uint16_t len);

void udp_handle_packet(const uint8_t src_mac[6], uint32_t src_ip, const uint8_t *data, uint16_t len);

#endif
