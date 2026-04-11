package com.urlshortener.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public record UrlResponse (
        String id,
        String url,
        String shortUrl,
        Instant createdAt,
        Instant lastModifiedAt,
        Long accessCount
) {
}
