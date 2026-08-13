#ifndef STRING_H
#define STRING_H

#include <stddef.h>
#include <stdint.h>

void *memset(void *dst, int val, size_t n);
void *memcpy(void *dst, const void *src, size_t n);
int memcmp(const void *a, const void *b, size_t n);
size_t strlen(const char *s);
uint16_t htons(uint16_t v);
uint32_t htonl(uint32_t v);
#define ntohs htons
#define ntohl htonl

#endif
