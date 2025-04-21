package com.mylearn.agentgate.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LoginInterceptorTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @BeforeEach
    public void setUp() {
        redisTemplate.opsForValue().set("user:token:" + "testUserToken", "testUserId", 30, TimeUnit.MINUTES);
    }

    @Test
    public void testShouldPass() throws Exception {
        // todo 好像除了登录接口一个接口也没写
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/testInterceptor")
                .header("token", "testUserToken"))
                .andExpect(status().isOk());
    }

    @Test
    public void testShouldBlock() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/testInterceptor")
                .header("token", "falseToken"))
                .andExpect(status().is4xxClientError());
    }

}