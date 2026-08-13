#ifndef NET_ICMP_H
#define NET_ICMP_H

#include <stdint.h>

void icmp_handle_packet(const uint8_t src_mac[6], uint32_t src_ip, const uint8_t *data, uint16_t len);

#endif
