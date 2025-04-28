package com.mylearn.agentgate.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 加密/校验工具类
 * 肯定热使用，登录就用，双检锁你妈，但是这不是单例模式啊
 */
public class PasswordUtil {
    private final static BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public static String hashPassword(String plainPassword) {
        return encoder.encode(plainPassword);
    }

    public static boolean matchPassword(String plainPassword, String hashPassword) {
        return encoder.matches(plainPassword, hashPassword);
    }
}
