#include "net.h"
#include "../kprintf.h"

uint8_t  g_mac[6];
uint32_t g_ip = 0;
uint32_t g_netmask = 0;
uint32_t g_gateway = 0;
uint32_t g_dns = 0;

const uint8_t ETH_BROADCAST[6] = {0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF};

void net_print_mac(const uint8_t mac[6]) {
    for (int i = 0; i < 6; i++) {
        if (mac[i] < 0x10) kprintf("0");
        kprintf("%x", mac[i]);
        if (i < 5) kprintf(":");
    }
}

void net_print_ip(uint32_t ip) {
    kprintf("%u.%u.%u.%u",
            (ip >> 24) & 0xFF, (ip >> 16) & 0xFF, (ip >> 8) & 0xFF, ip & 0xFF);
}
