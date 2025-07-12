package com.example.booksRedis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CasheService {
    private final RedisTemplate<String, Object> redisTemplate;

    public void caseObject(String key, Object object, Long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, object, timeout, timeUnit);
    }

    public Object getCashedObject(String key){
        return redisTemplate.opsForValue().get(key);
    }

    public void deleteCashedObject(String key){
        redisTemplate.delete(key);
    }

    public void incrementBookPopularity(Long id){
        redisTemplate.opsForZSet().incrementScore("books:popular", id.toString(), 1);
    }

    public Set<String> getPopularBookIds(int count) {
        Set<Object> rawIds = redisTemplate.opsForZSet().reverseRange("books:popular", 0, count - 1);
        return rawIds.stream()
                .map(Object::toString)
                .collect(Collectors.toSet());
    }

}
