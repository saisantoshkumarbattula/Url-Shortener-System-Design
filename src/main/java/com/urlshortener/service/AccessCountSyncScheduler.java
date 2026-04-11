package com.urlshortener.service;

import com.urlshortener.model.Url;
import com.urlshortener.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccessCountSyncScheduler {

    private final StringRedisTemplate stringRedisTemplate;
    private final UrlRepository urlRepository;

    @Scheduled(fixedDelay = 60000, initialDelay = 60000)
    public void run() {
        log.info("🔄 Starting Redis → MongoDB sync job");

        Set<String> keys = stringRedisTemplate.keys("url:clicks:*");
        if (keys == null || keys.isEmpty()) {
            log.info("No Redis keys found for sync");
            return;
        }
        log.info("Found {} keys to sync", keys.size());

        for (String key : keys) {
            String shortCode = key.replace("url:clicks:", "");
            String countStr = stringRedisTemplate.opsForValue().get(key);
            long count = countStr != null ? Long.parseLong(countStr) : 0L;

            if (count == 0) {
                log.debug("Skipping {} as count is 0", shortCode);
                continue;
            }
            log.info("Syncing shortCode: {} with count: {}", shortCode, count);
            Url url = urlRepository.findByShortCode(shortCode).orElse(null);

            if (url != null) {
                log.info("Updated DB for {} → new accessCount: {}", shortCode, url.getAccessCount());
                url.setAccessCount(url.getAccessCount() + count);
                urlRepository.save(url);
            } else {
                log.warn("No DB entry found for shortCode: {}", shortCode);
            }
            stringRedisTemplate.delete(key);
            log.debug("Deleted Redis key: {}", key);
        }
        log.info("✅ Redis → MongoDB sync job completed");
    }
}
