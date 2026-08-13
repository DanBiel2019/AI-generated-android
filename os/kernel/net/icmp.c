#include "icmp.h"
#include "ip.h"
#include "net.h"
#include "../string.h"
#include "../kprintf.h"

#define ICMP_ECHO_REQUEST 8
#define ICMP_ECHO_REPLY   0

struct icmp_hdr {
    uint8_t  type;
    uint8_t  code;
    uint16_t checksum;
    uint16_t id;
    uint16_t seq;
} __attribute__((packed));

#define MAX_ICMP 1500
static uint8_t reply_buf[MAX_ICMP];

void icmp_handle_packet(const uint8_t src_mac[6], uint32_t src_ip, const uint8_t *data, uint16_t len) {
    if (len < sizeof(struct icmp_hdr)) return;

    struct icmp_hdr hdr;
    memcpy(&hdr, data, sizeof(hdr));
    if (hdr.type != ICMP_ECHO_REQUEST) return;

    kprintf("icmp: echo request from ");
    net_print_ip(src_ip);
    kprintf(" id=%u seq=%u -- replying\n", ntohs(hdr.id), ntohs(hdr.seq));

    uint16_t total = len;
    if (total > MAX_ICMP) total = MAX_ICMP;
    memcpy(reply_buf, data, total);

    struct icmp_hdr *reply = (struct icmp_hdr *)reply_buf;
    reply->type = ICMP_ECHO_REPLY;
    reply->code = 0;
    reply->checksum = 0;
    reply->checksum = ip_checksum(reply_buf, total);

    ip_send(src_mac, src_ip, IP_PROTO_ICMP, reply_buf, total);
}
