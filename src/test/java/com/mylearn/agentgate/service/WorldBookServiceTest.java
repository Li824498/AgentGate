package com.mylearn.agentgate.service;

import com.mylearn.agentgate.utils.UserIdUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WorldBookServiceTest {

    @Test
    public void testThreadLocal() {
        UserIdUtils.setUserId("12345");

        System.out.println(Thread.currentThread().getName() + UserIdUtils.getUserId());

        String str = "correct";

        new Thread(new Runnable() {
            @Override
            public void run() {
                UserIdUtils.setUserId(str);

                System.out.println(str);

                System.out.println(Thread.currentThread().getName() + UserIdUtils.getUserId());
            }
        }).start();
    }

}