package com.mylearn.agentgate.service;

import com.mylearn.agentgate.utils.EmbedUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ModelApiServiceTest {
    @Autowired
    private ModelApiService modelApiService;

    @Autowired
    private EmbedUtils embedUtils;

    @Test
    public void testEmbApis() {
        String embApi = modelApiService.getEmbApi();
        System.out.println(embApi);

        System.setProperty("http.proxyHost", "127.0.0.1");
        System.setProperty("http.proxyPort", "7890");
        System.setProperty("https.proxyHost", "127.0.0.1");
        System.setProperty("https.proxyPort", "7890");

        System.out.println(embedUtils.embText2Embedding("nihao"));
    }

    @Test
    public void testChatApis() {
        String chatApi = modelApiService.getChatApi("gemini", "aaa");
        System.out.println(chatApi);
    }

}