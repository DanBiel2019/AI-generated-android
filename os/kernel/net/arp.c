#include "arp.h"
#include "eth.h"
#include "net.h"
#include "../string.h"
#include "../kprintf.h"

#define ARP_HTYPE_ETH   1
#define ARP_PTYPE_IPV4  0x0800
#define ARP_OP_REQUEST  1
#define ARP_OP_REPLY    2

void arp_handle_packet(const uint8_t *data, uint16_t len) {
    if (len < sizeof(struct arp_packet)) return;

    struct arp_packet pkt;
    memcpy(&pkt, data, sizeof(pkt));

    if (ntohs(pkt.htype) != ARP_HTYPE_ETH || ntohs(pkt.ptype) != ARP_PTYPE_IPV4) return;
    if (ntohs(pkt.oper) != ARP_OP_REQUEST) return;
    if (g_ip == 0 || ntohl(pkt.tpa) != g_ip) return; /* not asking about us */

    kprintf("arp: who-has ");
    net_print_ip(ntohl(pkt.tpa));
    kprintf(" -- replying\n");

    struct arp_packet reply;
    reply.htype = htons(ARP_HTYPE_ETH);
    reply.ptype = htons(ARP_PTYPE_IPV4);
    reply.hlen = 6;
    reply.plen = 4;
    reply.oper = htons(ARP_OP_REPLY);
    memcpy(reply.sha, g_mac, 6);
    reply.spa = htonl(g_ip);
    memcpy(reply.tha, pkt.sha, 6);
    reply.tpa = pkt.spa;

    eth_send(pkt.sha, ETHERTYPE_ARP, &reply, sizeof(reply));
}
