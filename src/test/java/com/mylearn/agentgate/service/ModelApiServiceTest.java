package com.mylearn.agentgate.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ModelApiServiceTest {
    @Autowired
    private ModelApiService modelApiService;

    @Test
    public void testEmbApis() {
        String embApi = modelApiService.getEmbApi();
        System.out.println(embApi);
    }

    @Test
    public void testChatApis() {
        String chatApi = modelApiService.getChatApi("gemini", "aaa");
        System.out.println(chatApi);
    }

}