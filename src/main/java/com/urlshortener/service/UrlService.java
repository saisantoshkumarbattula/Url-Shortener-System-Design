package com.urlshortener.service;

import com.urlshortener.dto.UrlRequest;
import com.urlshortener.dto.UrlResponse;
import com.urlshortener.exception.UrlNotFoundException;
import com.urlshortener.exception.UrlValidationFailureException;
import com.urlshortener.mapper.UrlMapper;
import com.urlshortener.model.Url;
import com.urlshortener.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {

    private final UrlRepository urlRepository;
    private final UrlMapper urlMapper;
    private static final String CHARSET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final StringRedisTemplate stringRedisTemplate;
    private final Duration DURATION = Duration.ofMinutes(10);


    public boolean isValidUrl(String url) {
        try {
            URI uri = new URI(url);
            return uri.getScheme() != null &&
                    (uri.getScheme().equals("http") || uri.getScheme().equals("https")) &&
                    uri.getHost() != null;
        } catch (Exception e) {
            return false;
        }
    }
    public UrlResponse shortenAndSave(UrlRequest urlRequest) {
        if(urlRequest.url() == null || urlRequest.url().isEmpty())
            throw new UrlValidationFailureException("Url required");
        if(!isValidUrl(urlRequest.url()))
            throw new UrlValidationFailureException("Invalid Url");

        Optional<Url> existingUrl = urlRepository.findByUrl(urlRequest.url());
        if(existingUrl.isPresent())
            return urlMapper.urlToUrlResponse(existingUrl.get());

        String shortCode = generateShortCode();
        while (urlRepository.findByShortCode(shortCode).isPresent()){
            shortCode = generateShortCode();
        }
        String key = "url:" + shortCode;
        stringRedisTemplate.opsForValue().set(key, urlRequest.url(), DURATION);
        Url url = Url.builder()
                .url(urlRequest.url())
                .shortCode(shortCode)
                .createdAt(Instant.now())
                .lastModifiedAt(Instant.now())
                .accessCount(0L)
                .build();

        return urlMapper.urlToUrlResponse(urlRepository.save(url));
    }

    private String generateShortCode() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            sb.append(CHARSET.charAt(random.nextInt(CHARSET.length())));
        }
        return sb.toString();
    }

    public UrlResponse getUrl(String shortCode) {

        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("Url does not exit or might have expired"));

        String key = "url:clicks" + shortCode;
        String redisCount = stringRedisTemplate.opsForValue().get(key);
        long accessCount = (redisCount != null) ? Long.parseLong(redisCount) : 0L;
        url.setAccessCount(accessCount);
        return urlMapper.urlToUrlResponse(url);
    }

    public UrlResponse updateUrl(String shortCode, UrlRequest urlRequest)  {

        Url urlFound = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("Url does not exist or might have expired"));

        if (shortCode == null || shortCode.isEmpty() || urlRequest.url() == null || urlRequest.url().isBlank()) {
            throw new UrlValidationFailureException("New URL cannot be empty");
        }
        urlFound.setUrl(urlRequest.url());
        urlFound.setLastModifiedAt(Instant.now());
        urlRepository.save(urlFound);
        return urlMapper.urlToUrlResponse(urlFound);
    }

    public void deleteUrl(String shortCode) {

        if (shortCode == null || shortCode.isBlank()) {
            throw new UrlValidationFailureException("Short code is required");
        }

        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("Url does not exist or might have expired"));

        urlRepository.delete(url);
    }

    public String getLongUrl(String shortCode) {

        String key = "url:" + shortCode;

        String longUrl = stringRedisTemplate.opsForValue().get(key);

        if (longUrl != null) {
            stringRedisTemplate.opsForValue().increment("url:clicks:" + shortCode);
            return longUrl;
        }

        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("Url does not exist or might have expired"));

        stringRedisTemplate.opsForValue()
                .set(key, url.getUrl(), Duration.ofMinutes(10));

        stringRedisTemplate.opsForValue().increment("url:clicks:" + shortCode);
        return url.getUrl();
    }
}
