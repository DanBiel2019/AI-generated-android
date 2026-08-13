#ifndef NET_DHCP_H
#define NET_DHCP_H

#include <stdint.h>

/* Broadcasts a DHCPDISCOVER. Call once after the NIC is up. */
void dhcp_send_discover(void);

/* Parses a DHCP reply (expects an OFFER) and, if valid, configures g_ip/g_netmask/g_gateway. */
void dhcp_handle_packet(const uint8_t *data, uint16_t len);

#endif
