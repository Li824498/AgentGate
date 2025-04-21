package com.mylearn.agentgate.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.TimeUnit;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//                return HandlerInterceptor.super.preHandle(request, response, handler);
        String token = request.getHeader("token");
        String userId = redisTemplate.opsForValue().get("user:token:" + token);
        if (userId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: token invalid or expired");
            return false;
        }

        redisTemplate.expire("user:token:" + token, 30, TimeUnit.MINUTES);
        UserIdUtils.setUserId(userId);
        return true;
    }
}
