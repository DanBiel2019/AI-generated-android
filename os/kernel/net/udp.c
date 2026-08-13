#include "udp.h"
#include "ip.h"
#include "dhcp.h"
#include "../string.h"

#define MAX_UDP 1500
static uint8_t tx_buf[MAX_UDP];

void udp_send(const uint8_t dst_mac[6], uint32_t dst_ip, uint16_t src_port, uint16_t dst_port,
              const void *payload, uint16_t len) {
    if (len > MAX_UDP - sizeof(struct udp_hdr)) len = MAX_UDP - sizeof(struct udp_hdr);

    struct udp_hdr *hdr = (struct udp_hdr *)tx_buf;
    hdr->src_port = htons(src_port);
    hdr->dst_port = htons(dst_port);
    hdr->length = htons((uint16_t)(sizeof(struct udp_hdr) + len));
    hdr->checksum = 0; /* optional for IPv4; 0 = not computed */

    memcpy(tx_buf + sizeof(struct udp_hdr), payload, len);
    ip_send(dst_mac, dst_ip, IP_PROTO_UDP, tx_buf, (uint16_t)(sizeof(struct udp_hdr) + len));
}

void udp_handle_packet(const uint8_t src_mac[6], uint32_t src_ip, const uint8_t *data, uint16_t len) {
    if (len < sizeof(struct udp_hdr)) return;

    struct udp_hdr hdr;
    memcpy(&hdr, data, sizeof(hdr));

    uint16_t dst_port = ntohs(hdr.dst_port);
    uint16_t src_port = ntohs(hdr.src_port);
    const uint8_t *payload = data + sizeof(struct udp_hdr);
    uint16_t plen = (uint16_t)(len - sizeof(struct udp_hdr));

    if (dst_port == 68 && src_port == 67) {
        dhcp_handle_packet(payload, plen);
        (void)src_mac;
        (void)src_ip;
    }
}
