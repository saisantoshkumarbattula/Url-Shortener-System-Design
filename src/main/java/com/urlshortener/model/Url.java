package com.urlshortener.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "urls")
@Builder
public class Url {
    @Id
    private String id;
    private String url;
    private String shortCode;
    private Instant createdAt;
    private Instant lastModifiedAt;
    private Long accessCount;
}
