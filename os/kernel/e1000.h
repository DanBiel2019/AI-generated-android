#ifndef E1000_H
#define E1000_H

#include <stdint.h>

/*
 * Detects and brings up an Intel e1000-family NIC over PCI.
 * On success, fills mac_out[6] with the card's MAC address and
 * returns 1. Returns 0 if no supported NIC was found.
 */
int e1000_init(uint8_t mac_out[6]);

/* Transmits a raw Ethernet frame (caller supplies the full frame incl. header). */
void e1000_send(const void *data, uint16_t len);

/*
 * Polls for a received frame. Returns the frame length and copies it into
 * buf (up to maxlen bytes) if one is ready, or returns 0 if nothing is
 * waiting right now. Non-blocking -- call this in a loop.
 */
uint16_t e1000_poll_receive(void *buf, uint16_t maxlen);

#endif
