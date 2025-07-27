package com.mylearn.agentgate.utils;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class nornaltest {
    @Test
    public void test1() {
        System.out.println("user在如下时间发送了如下消息: 2025-07-27 20:01:50  ".length());

        System.out.println("user在如下时间发送了如下消息: 2025-07-27 20:01:50  asd".substring(38));
        System.out.println("user在如下时间发送了如下消息: 2025-07-27 20:01:50  asd".substring(39));
        System.out.println("user在如下时间发送了如下消息: 2025-07-27 20:01:50  asd".substring(40));
    }
}
