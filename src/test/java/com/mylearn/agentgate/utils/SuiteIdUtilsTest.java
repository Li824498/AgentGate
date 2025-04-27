package com.mylearn.agentgate.utils;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class SuiteIdUtilsTest {
    @Autowired
    private SuiteIdUtils suiteIdUtils;

    @Test
    public void testUtils() {
        int l = suiteIdUtils.nextId();
        System.out.println(l);
    }

}