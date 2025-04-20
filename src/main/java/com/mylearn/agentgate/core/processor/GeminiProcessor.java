package com.mylearn.agentgate.core.processor;

import com.mylearn.agentgate.annoation.ModelType;
import com.mylearn.agentgate.core.domain.history.GeminiHistory;
import com.mylearn.agentgate.core.entity.HistoryMessage;
import com.mylearn.agentgate.core.entity.LRequest;
import com.mylearn.agentgate.core.entity.LResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
@ModelType("gemini")
public class GeminiProcessor extends AbstractChatProcessor{

    @Autowired
    private GeminiHistory history;

    @Override
    List<HistoryMessage> historyBefore(LRequest lRequest) {
        List<HistoryMessage> historyMessages = history.processBefore(lRequest);
        return historyMessages;
    }


    @Override
    void prompt(LRequest lRequest) {
        
    }

    @Override
    void worldBook(LRequest lRequest) {

    }

    @Override
    void historyAfter(LResponse lResponse) {
        history.processAfter(lResponse);
    }

    @Override
    LResponse transferAi(LRequest lRequest, RestTemplate restTemplate, List<HistoryMessage> history) {
        // todo 负载均衡设计 可能采用配置方式解决
        String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=AIzaSyBdRTvIyopn0zc1z_uenRPVzO8cMapm_pI";


//        HttpEntity<Map<String, Object>> entity = sendGeminiText(lRequest);
        HttpEntity<Map<String, Object>> entity = sendGeminiTextWithHistory(lRequest, history);
        ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, Map.class);
        String text = getGeminiText(response);

        // todo 建造者模式优化
        LResponse lResponse = new LResponse();
        lResponse.setContext(text);
        lResponse.setUserId(lRequest.getUserId());
        lResponse.setChatId(lRequest.getChatId());
        lResponse.setMsgIndex(lRequest.getMsgIndex() + 1);

        return lResponse;
    }

    private HttpEntity<Map<String, Object>> sendGeminiTextWithHistory(LRequest lRequest, List<HistoryMessage> history) {
        List<Map> contents = new ArrayList<>();
        // todo Map有上限，修掉这个bug
        for (HistoryMessage historyMessage : history) {
            List<Map<String, String>> parts = List.of(Map.of("text", historyMessage.getContext()));

            Map<String, Object> map = new LinkedHashMap<>();
            map.put("role", historyMessage.getRole());
            map.put("parts", parts);

            contents.add(map);
        }

        List<Map<String, String>> partsNow = new ArrayList<>();
        partsNow.add(Map.of("text", lRequest.getContext()));

        Map<String, Object> mapNow = new LinkedHashMap<>();
        mapNow.put("role", "user");
        mapNow.put("parts", partsNow);

        contents.add(mapNow);

        Map<String, Object> body = Map.of("contents", contents);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, httpHeaders);

        return entity;
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
        Map<String, Object> part = Map.of("text", lRequest.getContext());

        Map<String, Object> message = Map.of("parts", List.of(part));

        Map<String, Object> body = Map.of("contents", List.of(message));


        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, httpHeaders);

        return entity;
    }
}
