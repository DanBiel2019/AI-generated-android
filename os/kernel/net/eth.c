#include "eth.h"
#include "net.h"
#include "arp.h"
#include "ip.h"
#include "../e1000.h"
#include "../string.h"
#include "../kprintf.h"

#define MAX_FRAME 1518

static uint8_t tx_frame[MAX_FRAME];
static uint8_t rx_frame[MAX_FRAME];

void eth_send(const uint8_t dst_mac[6], uint16_t ethertype, const void *payload, uint16_t len) {
    if (len > MAX_FRAME - sizeof(struct eth_hdr)) len = MAX_FRAME - sizeof(struct eth_hdr);

    struct eth_hdr *hdr = (struct eth_hdr *)tx_frame;
    memcpy(hdr->dst, dst_mac, 6);
    memcpy(hdr->src, g_mac, 6);
    hdr->ethertype = htons(ethertype);

    memcpy(tx_frame + sizeof(struct eth_hdr), payload, len);
    e1000_send(tx_frame, (uint16_t)(sizeof(struct eth_hdr) + len));
}

void eth_poll(void) {
    uint16_t len = e1000_poll_receive(rx_frame, MAX_FRAME);
    if (len < sizeof(struct eth_hdr)) return;

    struct eth_hdr *hdr = (struct eth_hdr *)rx_frame;
    uint16_t ethertype = ntohs(hdr->ethertype);
    const uint8_t *payload = rx_frame + sizeof(struct eth_hdr);
    uint16_t plen = len - sizeof(struct eth_hdr);

    switch (ethertype) {
        case ETHERTYPE_ARP:
            arp_handle_packet(payload, plen);
            break;
        case ETHERTYPE_IPV4:
            ip_handle_packet(hdr->src, payload, plen);
            break;
        default:
            break;
    }
}
