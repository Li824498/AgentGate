package com.mylearn.agentgate.service.model;

import com.mylearn.agentgate.annoation.ModelType;
import com.mylearn.agentgate.entity.ChatHistory;
import com.mylearn.agentgate.entity.LRequest;
import com.mylearn.agentgate.entity.LResponse;
import com.mylearn.agentgate.utils.ModelJsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Component
@ModelType("genimi")
public class GenimiModel implements LanguageModel{
    @Override
    public void sendLRequest(LRequest lRequest, ChatHistory chatHistory, RestTemplate restTemplate) {
        log.info("发送了genimi消息");
        String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=AIzaSyBdRTvIyopn0zc1z_uenRPVzO8cMapm_pI";


        Map<String, Object> part = Map.of("text", lRequest.getText());

        Map<String, Object> message = Map.of("parts", List.of(part));

        Map<String, Object> body = Map.of("contents", List.of(message));


        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, httpHeaders);

        ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, Map.class);

        String text = ModelJsonUtil.getGenimiText(response);

        System.out.println("模型回复内容为: " + text);
        System.out.println(response.getBody().toString());

    }

    @Override
    public LResponse getLResponse(int uid_chat, int uid_position) {
        log.info("接受了genimi消息");
        return null;
    }
}
