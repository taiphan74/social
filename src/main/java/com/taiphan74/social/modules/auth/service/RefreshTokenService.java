package com.taiphan74.social.modules.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${app.jwt.refresh-expiration}")
    private long refreshExpirationMs;

    private static final String PREFIX = "refresh:";

    public void save(String userId, String familyId, String tokenId) {
        String key = PREFIX + userId + ":" + familyId;
        redisTemplate.opsForValue().set(key, tokenId, refreshExpirationMs, TimeUnit.MILLISECONDS);
    }

    public boolean validateAndRotate(String userId, String familyId, String oldTokenId, String newTokenId) {
        String key = PREFIX + userId + ":" + familyId;
        String storedTokenId = (String) redisTemplate.opsForValue().get(key);

        if (storedTokenId == null || !storedTokenId.equals(oldTokenId)) {
            deleteFamily(userId, familyId);
            return false;
        }
        redisTemplate.opsForValue().set(key, newTokenId, refreshExpirationMs, TimeUnit.MILLISECONDS);
        return true;
    }

    public void deleteFamily(String userId, String familyId) {
        String key = PREFIX + userId + ":" + familyId;
        redisTemplate.delete(key);
    }

    public void deleteAllForUser(String userId) {
        Set<String> keys = redisTemplate.keys(PREFIX + userId + ":*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
