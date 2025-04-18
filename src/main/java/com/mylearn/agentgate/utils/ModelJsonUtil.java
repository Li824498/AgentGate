package com.mylearn.agentgate.utils;

import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public class ModelJsonUtil {
    public static String getGenimiText(ResponseEntity<Map> response) {
        Map<String, List> responseBody = response.getBody();
        List<Map> candidates = responseBody.get("candidates");
        Map<String, Map> firstCandidate = candidates.get(0);
        Map<String, List> contents = firstCandidate.get("content");
        List<Map> parts = contents.get("parts");
        Map<String, String> firstPart = parts.get(0);
        String text = firstPart.get("text");

        return text;
    }
}
