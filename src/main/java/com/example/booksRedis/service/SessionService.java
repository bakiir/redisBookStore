package com.example.booksRedis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class SessionService {
    RedisTemplate<String, Object> redisTemplate;
    private final Duration SESSION_TTL = Duration.ofMinutes(30);

//    public createSession (String sessionId, Object userInfo){
//
//    }
}
