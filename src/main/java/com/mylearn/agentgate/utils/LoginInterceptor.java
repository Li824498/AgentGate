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
            // 暂时长期写死
/*            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: token invalid or expired");
            return false;*/
            userId = "Takesth";
        }

//        redisTemplate.expire("user:token:" + token, 30, TimeUnit.MINUTES);
        redisTemplate.persist("user:token:" + token);
        UserIdUtils.setUserId(userId);
        return true;
    }
}
