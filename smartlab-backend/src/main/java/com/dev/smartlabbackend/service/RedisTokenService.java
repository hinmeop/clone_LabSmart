package com.dev.smartlabbackend.service;

import com.dev.smartlabbackend.dto.response.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisTokenService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${app.redis.token-blacklist-prefix:smartlab:blacklist:token:}")
    private String blacklistPrefix;

    @Value("${app.redis.user-cache-prefix:smartlab:user:profile:}")
    private String userCachePrefix;

    @Value("${app.redis.cache-ttl-minutes:60}")
    private long cacheTtlMinutes;

    public void blacklistToken(String token, long remainingTtlMs) {
        if (remainingTtlMs <= 0) {
            return;
        }
        try {
            String key = blacklistPrefix + token;
            redisTemplate.opsForValue().set(key, "revoked", remainingTtlMs, TimeUnit.MILLISECONDS);
            log.info("Token successfully blacklisted in Redis for {} ms", remainingTtlMs);
        } catch (Exception e) {
            log.warn("Failed to blacklist token in Redis: {}", e.getMessage());
        }
    }

    public boolean isTokenBlacklisted(String token) {
        try {
            String key = blacklistPrefix + token;
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.warn("Redis check for blacklisted token failed (allowing request): {}", e.getMessage());
            return false;
        }
    }

    public void cacheUserProfile(UserProfileResponse profile) {
        if (profile == null || profile.getId() == null) {
            return;
        }
        try {
            String key = userCachePrefix + profile.getId();
            redisTemplate.opsForValue().set(key, profile, cacheTtlMinutes, TimeUnit.MINUTES);
            log.debug("User profile cached in Redis for userId: {}", profile.getId());
        } catch (Exception e) {
            log.warn("Failed to cache user profile in Redis: {}", e.getMessage());
        }
    }

    public UserProfileResponse getCachedUserProfile(Long userId) {
        if (userId == null) {
            return null;
        }
        try {
            String key = userCachePrefix + userId;
            Object obj = redisTemplate.opsForValue().get(key);
            if (obj instanceof UserProfileResponse profile) {
                log.debug("Cache hit in Redis for user profile userId: {}", userId);
                return profile;
            }
        } catch (Exception e) {
            log.warn("Failed to get cached user profile from Redis: {}", e.getMessage());
        }
        return null;
    }

    public void evictUserProfile(Long userId) {
        if (userId == null) {
            return;
        }
        try {
            String key = userCachePrefix + userId;
            redisTemplate.delete(key);
            log.debug("Evicted user profile cache for userId: {}", userId);
        } catch (Exception e) {
            log.warn("Failed to evict user profile from Redis: {}", e.getMessage());
        }
    }
}
