package com.mylearn.agentgate.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {

    @Test
    public void testHashAndMatch() {
        String plainPassword = "123456";

        String hashPassword = PasswordUtil.hashPassword(plainPassword);
        System.out.println(hashPassword);

        System.out.println("看看规则");
        System.out.println(PasswordUtil.matchPassword(plainPassword, hashPassword));

        System.out.println(PasswordUtil.matchPassword(plainPassword, plainPassword));
    }

}