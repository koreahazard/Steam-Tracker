package com.example.steam_tracker.redis;

public interface RedisService {

    // 저장 (TTL 포함)
    void save(String key, String value, long ttlMs);

    // 조회
    String get(String key);

    // 삭제
    void delete(String key);

    // 존재 여부
    boolean exists(String key);
}
