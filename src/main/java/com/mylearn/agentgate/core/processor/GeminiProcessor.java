package com.mylearn.agentgate.core.processor;

import com.mylearn.agentgate.annoation.ModelType;
import com.mylearn.agentgate.core.entity.LRequest;
import com.mylearn.agentgate.core.entity.LResponse;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
@ModelType("gemini")
public class GeminiProcessor extends AbstractChatProcessor{
    @Override
    void history(LRequest lRequest) {

    }

    @Override
    void prompt(LRequest lRequest) {

    }

    @Override
    void worldBook(LRequest lRequest) {

    }

    @Override
    LResponse transferAi(LRequest lRequest, RestTemplate restTemplate) {
        // todo 负载均衡设计 可能采用配置方式解决
        String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=AIzaSyBdRTvIyopn0zc1z_uenRPVzO8cMapm_pI";


        HttpEntity<Map<String, Object>> entity = sendGeminiText(lRequest);
        ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, Map.class);
        String text = getGeminiText(response);

        // todo 建造者模式优化
        LResponse lResponse = new LResponse();
        lResponse.setText(text);
        lResponse.setUid_chat(lRequest.getUid_chat());
        lResponse.setUid_position(lRequest.getUid_position());

        return lResponse;
    }


    public static String getGeminiText(ResponseEntity<Map> response) {
        Map<String, List> responseBody = response.getBody();
        List<Map> candidates = responseBody.get("candidates");
        Map<String, Map> firstCandidate = candidates.get(0);
        Map<String, List> contents = firstCandidate.get("content");
        List<Map> parts = contents.get("parts");
        Map<String, String> firstPart = parts.get(0);
        String text = firstPart.get("text");

        return text;
    }

    // todo 重新设计兼容三大模块
    public static HttpEntity<Map<String, Object>> sendGeminiText(LRequest lRequest) {
        Map<String, Object> part = Map.of("text", lRequest.getText());

        Map<String, Object> message = Map.of("parts", List.of(part));

        Map<String, Object> body = Map.of("contents", List.of(message));


        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, httpHeaders);

        return entity;
    }
}
