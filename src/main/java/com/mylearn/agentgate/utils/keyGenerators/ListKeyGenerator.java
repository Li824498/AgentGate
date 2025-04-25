package com.mylearn.agentgate.utils.keyGenerators;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ListKeyGenerator implements KeyGenerator {
    @Override
    public Object generate(Object target, Method method, Object... params) {
        List<Long> list = (List<Long>) params[0];
        List<Long> collect = list.stream().sorted().collect(Collectors.toList());

        String key = collect.toString();
        log.info("生成缓存键:" + key);
        return key;
    }
}
