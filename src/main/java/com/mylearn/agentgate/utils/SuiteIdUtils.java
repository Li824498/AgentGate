package com.mylearn.agentgate.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class SuiteIdUtils {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String SUITE_ID_INCR = "suite:id:generator";

    public int nextId() {
        return stringRedisTemplate.opsForValue().increment(SUITE_ID_INCR).intValue();
    }
}
