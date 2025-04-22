package com.mylearn.agentgate.service;

import com.mylearn.agentgate.mapper.UserMapper;
import com.mylearn.agentgate.utils.PasswordUtil;
import com.mylearn.agentgate.utils.UserIdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private UserMapper userMapper;

    public String login(String userId, String password) {
        String plainPassword = password;
        // todo 不存在用户为空的情况
        String hashPassword = userMapper.selectById(userId);

        if (!PasswordUtil.matchPassword(plainPassword, hashPassword)) {
            return null;
        }

        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set("user:token:" + token, userId, 30, TimeUnit.MINUTES);
        UserIdUtils.setUserId(userId);

        return token;
    }
}
