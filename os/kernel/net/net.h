#ifndef NET_NET_H
#define NET_NET_H

#include <stdint.h>

/* Global interface state. IPs are stored in host byte order. */
extern uint8_t  g_mac[6];
extern uint32_t g_ip;       /* 0 until DHCP completes */
extern uint32_t g_netmask;
extern uint32_t g_gateway;
extern uint32_t g_dns;

extern const uint8_t ETH_BROADCAST[6];
#define IP_BROADCAST 0xFFFFFFFFu

void net_print_mac(const uint8_t mac[6]);
void net_print_ip(uint32_t ip);

#endif
