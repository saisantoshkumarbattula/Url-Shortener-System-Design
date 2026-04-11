package com.urlshortener.mapper;

import com.urlshortener.dto.UrlResponse;
import com.urlshortener.model.Url;
import org.springframework.stereotype.Component;

@Component
public class UrlMapper {

    public UrlResponse urlToUrlResponse(Url url) {
        String PROTOCOL = "https://";
        String HOST = "short.com";
        String PATH = "/";
        return UrlResponse.builder()
                .id(url.getId())
                .url(url.getUrl())
                .shortUrl(PROTOCOL + HOST + PATH + url.getShortCode())
                .createdAt(url.getCreatedAt())
                .lastModifiedAt(url.getLastModifiedAt())
                .accessCount(url.getAccessCount())
                .build();
    }
}
