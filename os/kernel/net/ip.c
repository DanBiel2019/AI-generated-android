#include "ip.h"
#include "eth.h"
#include "net.h"
#include "icmp.h"
#include "udp.h"
#include "../string.h"

#define MAX_IP_PACKET 1500

static uint8_t tx_packet[MAX_IP_PACKET];

uint16_t ip_checksum(const void *data, uint16_t len) {
    const uint16_t *p = (const uint16_t *)data;
    uint32_t sum = 0;

    while (len > 1) {
        sum += *p++;
        len -= 2;
    }
    if (len == 1) sum += *(const uint8_t *)p;

    while (sum >> 16) sum = (sum & 0xFFFF) + (sum >> 16);
    return (uint16_t)~sum;
}

void ip_send(const uint8_t dst_mac[6], uint32_t dst_ip, uint8_t protocol,
             const void *payload, uint16_t len) {
    if (len > MAX_IP_PACKET - sizeof(struct ipv4_hdr)) len = MAX_IP_PACKET - sizeof(struct ipv4_hdr);

    struct ipv4_hdr *hdr = (struct ipv4_hdr *)tx_packet;
    hdr->ver_ihl = 0x45; /* version 4, header length 5*4=20 bytes */
    hdr->tos = 0;
    hdr->total_len = htons((uint16_t)(sizeof(struct ipv4_hdr) + len));
    hdr->id = 0;
    hdr->flags_frag = 0;
    hdr->ttl = 64;
    hdr->protocol = protocol;
    hdr->checksum = 0;
    hdr->src = htonl(g_ip);
    hdr->dst = htonl(dst_ip);
    hdr->checksum = ip_checksum(hdr, sizeof(struct ipv4_hdr));

    memcpy(tx_packet + sizeof(struct ipv4_hdr), payload, len);
    eth_send(dst_mac, ETHERTYPE_IPV4, tx_packet, (uint16_t)(sizeof(struct ipv4_hdr) + len));
}

void ip_handle_packet(const uint8_t src_mac[6], const uint8_t *data, uint16_t len) {
    if (len < sizeof(struct ipv4_hdr)) return;

    struct ipv4_hdr hdr;
    memcpy(&hdr, data, sizeof(hdr));

    uint8_t ihl = (hdr.ver_ihl & 0x0F) * 4;
    if (ihl < sizeof(struct ipv4_hdr) || len < ihl) return;

    uint32_t dst = ntohl(hdr.dst);
    /* Accept broadcast, packets addressed to us, or anything while we're
       still unconfigured (needed to see our own DHCP offer arrive). */
    if (g_ip != 0 && dst != g_ip && dst != IP_BROADCAST) return;

    const uint8_t *payload = data + ihl;
    uint16_t total = ntohs(hdr.total_len);
    uint16_t plen = (total > ihl) ? (uint16_t)(total - ihl) : 0;
    if (plen > len - ihl) plen = (uint16_t)(len - ihl);

    switch (hdr.protocol) {
        case IP_PROTO_ICMP:
            icmp_handle_packet(src_mac, ntohl(hdr.src), payload, plen);
            break;
        case IP_PROTO_UDP:
            udp_handle_packet(src_mac, ntohl(hdr.src), payload, plen);
            break;
        default:
            break;
    }
}
