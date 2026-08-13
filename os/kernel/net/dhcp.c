/*
 * Minimal DHCP client: broadcasts DISCOVER, accepts the first OFFER it
 * sees and applies it directly. A conformant client would follow up with
 * REQUEST/ACK before using the address; this v0.1 skips that handshake
 * since the goal here is proving the Ethernet/IP/UDP/DHCP round trip
 * through the NIC, not a production-correct lease protocol. Good enough
 * to talk to QEMU's slirp DHCP server; do the real handshake before this
 * touches a network with other DHCP clients on it.
 */

#include "dhcp.h"
#include "udp.h"
#include "net.h"
#include "../string.h"
#include "../kprintf.h"

#define DHCP_SERVER_PORT 67
#define DHCP_CLIENT_PORT 68
#define DHCP_MAGIC_COOKIE 0x63825363u

#define DHCP_OP_BOOTREQUEST 1
#define DHCP_HTYPE_ETH 1

#define DHCP_MSG_DISCOVER 1
#define DHCP_MSG_OFFER    2

/* Fixed BOOTP header + magic cookie. Options are variable-length and follow
   immediately after in the wire buffer -- handled via pointer, not copied
   into this struct, so there's no truncation/overread risk regardless of
   how many options a server sends. */
struct dhcp_packet {
    uint8_t  op, htype, hlen, hops;
    uint32_t xid;
    uint16_t secs, flags;
    uint32_t ciaddr, yiaddr, siaddr, giaddr;
    uint8_t  chaddr[16];
    uint8_t  sname[64];
    uint8_t  file[128];
    uint32_t magic_cookie;
} __attribute__((packed));

#define DHCP_OPTIONS_BUF 312 /* generous fixed buffer for outgoing DISCOVER options */

static uint32_t dhcp_xid = 0x1A0B0C0Du;

void dhcp_send_discover(void) {
    static uint8_t buf[sizeof(struct dhcp_packet) + DHCP_OPTIONS_BUF];
    memset(buf, 0, sizeof(buf));

    struct dhcp_packet *pkt = (struct dhcp_packet *)buf;
    pkt->op = DHCP_OP_BOOTREQUEST;
    pkt->htype = DHCP_HTYPE_ETH;
    pkt->hlen = 6;
    pkt->xid = htonl(dhcp_xid);
    pkt->flags = htons(0x8000); /* ask for a broadcast reply, we have no IP yet */
    memcpy(pkt->chaddr, g_mac, 6);
    pkt->magic_cookie = htonl(DHCP_MAGIC_COOKIE);

    uint8_t *options = buf + sizeof(struct dhcp_packet);
    int i = 0;
    options[i++] = 53; options[i++] = 1; options[i++] = DHCP_MSG_DISCOVER;
    options[i++] = 55; options[i++] = 3; options[i++] = 1; options[i++] = 3; options[i++] = 6;
    options[i++] = 255;

    kprintf("dhcp: sending DISCOVER (xid=0x%x)\n", dhcp_xid);
    udp_send(ETH_BROADCAST, IP_BROADCAST, DHCP_CLIENT_PORT, DHCP_SERVER_PORT,
             buf, (uint16_t)(sizeof(struct dhcp_packet) + i));
}

static int find_option(const uint8_t *opts, uint16_t opts_len, uint8_t code, const uint8_t **val, uint8_t *val_len) {
    uint16_t i = 0;
    while (i + 2 <= opts_len) {
        uint8_t opt = opts[i];
        if (opt == 255) break;
        if (opt == 0) { i++; continue; }
        uint8_t len = opts[i + 1];
        if (i + 2 + len > opts_len) break;
        if (opt == code) {
            *val = &opts[i + 2];
            *val_len = len;
            return 1;
        }
        i += 2 + len;
    }
    return 0;
}

void dhcp_handle_packet(const uint8_t *data, uint16_t len) {
    if (len < sizeof(struct dhcp_packet)) return;

    struct dhcp_packet pkt;
    memcpy(&pkt, data, sizeof(pkt));

    if (ntohl(pkt.magic_cookie) != DHCP_MAGIC_COOKIE) return;
    if (ntohl(pkt.xid) != dhcp_xid) return;

    /* Options start right after the fixed header in the original buffer;
       read them by pointer so arbitrarily long option lists are handled
       without truncation. */
    const uint8_t *opts = data + sizeof(struct dhcp_packet);
    uint16_t opts_len = (uint16_t)(len - sizeof(struct dhcp_packet));

    const uint8_t *val;
    uint8_t val_len;

    if (!find_option(opts, opts_len, 53, &val, &val_len) || val_len < 1 || val[0] != DHCP_MSG_OFFER) {
        return;
    }

    uint32_t offered_ip = ntohl(pkt.yiaddr);
    kprintf("dhcp: OFFER received, yiaddr=");
    net_print_ip(offered_ip);

    uint32_t netmask = 0xFFFFFF00u; /* fallback /24 */
    if (find_option(opts, opts_len, 1, &val, &val_len) && val_len == 4) {
        memcpy(&netmask, val, 4);
        netmask = ntohl(netmask);
    }
    uint32_t gateway = 0;
    if (find_option(opts, opts_len, 3, &val, &val_len) && val_len >= 4) {
        memcpy(&gateway, val, 4);
        gateway = ntohl(gateway);
    }
    uint32_t dns = 0;
    if (find_option(opts, opts_len, 6, &val, &val_len) && val_len >= 4) {
        memcpy(&dns, val, 4);
        dns = ntohl(dns);
    }

    kprintf(" netmask=");
    net_print_ip(netmask);
    kprintf(" gateway=");
    net_print_ip(gateway);
    kprintf(" dns=");
    net_print_ip(dns);
    kprintf("\n");

    g_ip = offered_ip;
    g_netmask = netmask;
    g_gateway = gateway;
    g_dns = dns;

    kprintf("dhcp: interface configured, address=");
    net_print_ip(g_ip);
    kprintf(" (offer applied without REQUEST/ACK -- see dhcp.c)\n");
}
