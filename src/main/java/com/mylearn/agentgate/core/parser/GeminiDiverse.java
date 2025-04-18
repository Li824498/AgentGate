package com.mylearn.agentgate.core.parser;

import com.mylearn.agentgate.core.entity.LRequest;
import org.springframework.http.*;

import java.util.List;
import java.util.Map;

public class GeminiDiverse implements Diverse{
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
        // todo 负载均衡设计
        String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=AIzaSyBdRTvIyopn0zc1z_uenRPVzO8cMapm_pI";


        Map<String, Object> part = Map.of("text", lRequest.getText());

        Map<String, Object> message = Map.of("parts", List.of(part));

        Map<String, Object> body = Map.of("contents", List.of(message));


        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, httpHeaders);

        return entity;
    }
}
