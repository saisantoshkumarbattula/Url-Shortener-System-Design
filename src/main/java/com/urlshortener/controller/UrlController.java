package com.urlshortener.controller;

import com.urlshortener.dto.UrlRequest;
import com.urlshortener.dto.UrlResponse;
import com.urlshortener.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/shorten")
@RequiredArgsConstructor
@Slf4j
public class UrlController {

    private final UrlService urlService;

    @PostMapping
    public ResponseEntity<UrlResponse> shortenUrl(@RequestBody UrlRequest urlRequest) {
        log.info(urlRequest.url());
        return new ResponseEntity<>(urlService.shortenAndSave(urlRequest), HttpStatus.CREATED);
    }

    @GetMapping("/{shortCode}/stats")
    public ResponseEntity<UrlResponse> getUrl(@PathVariable String shortCode){
        log.info(shortCode);
        return new ResponseEntity<>(urlService.getUrl(shortCode), HttpStatus.OK);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        log.info(shortCode);
        String longUrl = urlService.getLongUrl(shortCode);

        return ResponseEntity
                .status(HttpStatus.FOUND) // 302
                .location(URI.create(longUrl))
                .build();
    }

    @PutMapping("/{shortCode}")
    public ResponseEntity<UrlResponse> updateUrl(@PathVariable String shortCode, @RequestBody UrlRequest urlRequest) {
        log.info(urlRequest.url());
        return new ResponseEntity<>(urlService.updateUrl(shortCode, urlRequest), HttpStatus.OK);
    }

    @DeleteMapping("/{shortCode}")
    public ResponseEntity<Void> deleteUrl(@PathVariable String shortCode) {
        urlService.deleteUrl(shortCode);
        return ResponseEntity.noContent().build();
    }
}
