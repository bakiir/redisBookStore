package com.example.booksRedis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final Duration SESSION_TTL = Duration.ofMinutes(30);

    public void createSession(String sessionId, Object userInfo) {
        redisTemplate.opsForValue().set("session:" + sessionId, userInfo, SESSION_TTL);
    }

    public Object getSession(String sessionId) {
        // Обновим TTL при активности
        String key = "session:" + sessionId;
        Object session = redisTemplate.opsForValue().get(key);
        if (session != null) {
            redisTemplate.expire(key, SESSION_TTL);
        }
        return session;
    }

    public void deleteSession(String sessionId) {
        redisTemplate.delete("session:" + sessionId);
    }

    public Long getSessionTTL(String sessionId) {
        return redisTemplate.getExpire("session:" + sessionId, TimeUnit.SECONDS);
    }
}
