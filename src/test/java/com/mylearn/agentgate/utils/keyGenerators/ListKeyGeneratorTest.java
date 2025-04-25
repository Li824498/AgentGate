package com.mylearn.agentgate.utils.keyGenerators;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ListKeyGeneratorTest {

    @Test
    public void LKGTest() {
        List<Integer> integers = List.of(1, 4, 2, 6, 3);
        System.out.println(integers.toString());
    }

}