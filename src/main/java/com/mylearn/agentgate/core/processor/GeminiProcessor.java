package com.mylearn.agentgate.core.processor;

import com.mylearn.agentgate.annoation.ModelType;
import com.mylearn.agentgate.core.domain.history.GeminiHistoryManager;
import com.mylearn.agentgate.core.domain.prompt.PromptManager;
import com.mylearn.agentgate.core.domain.roleCard.RoleCardManager;
import com.mylearn.agentgate.core.entity.*;
import com.mylearn.agentgate.exception.AgentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
@ModelType(value = "gemini")
public class GeminiProcessor extends AbstractChatProcessor {

    @Autowired
    private GeminiHistoryManager history;

    @Autowired
    private PromptManager promptManager;

    @Autowired
    private RoleCardManager roleCardManager;



    @Override
    List<HistoryMessage> historyBefore(LRequest lRequest) {
        List<HistoryMessage> historyMessages = history.processBefore(lRequest);
        return historyMessages;
    }


    @Override
    Prompt prompt(LRequest lRequest) {
        Prompt prompt = promptManager.process(lRequest);
        return prompt;
    }

    @Override
    RoleCard roleCard(LRequest lRequest) {
        return roleCardManager.process(lRequest);
    }

    @Override
    void worldBook(LRequest lRequest) {

    }

    @Override
    void historyAfter(LResponse lResponse) {
        history.processAfter(lResponse);
    }

    @Override
    void chatMetaBefore(LRequest lRequest) {
        history.chatMetaProcessBefore(lRequest);
    }

    @Override
    void chatMetaAfter(LRequest lRequest, LResponse lResponse) {
        history.chatMetaProcessAfter(lRequest, lResponse);
    }

    @Override
    LResponse transferAi(LRequest lRequest, RestTemplate restTemplate, List<HistoryMessage> history, Prompt prompt, RoleCard roleCard) {
        // todo 负载均衡设计 可能采用配置方式解决
        String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=AIzaSyAMwBIWE63VgdEmhu1FcDR4bCMUa2w7u0E";

        // todo 分情况模式的
//        HttpEntity<Map<String, Object>> entity = sendGeminiText(lRequest);
//        HttpEntity<Map<String, Object>> entity = sendGeminiTextWithHistory(lRequest, history);
//        HttpEntity<Map<String, Object>> entity = sendGeminiTextWithHistoryAndPrompt(lRequest, history, prompt);

        HttpEntity<Map<String, Object>> entity = null;

/*        if(history.isEmpty()) {
            entity = sendGeminiText(lRequest);
        } else if(prompt == null) {
            entity = sendGeminiTextWithHistory(lRequest, history);
        } else {
            entity = sendGeminiTextWithHistoryAndPrompt(lRequest, history, prompt);
        }*/

        entity = sendGemini(lRequest, history, prompt, roleCard);

        ResponseEntity<Map> response = null;
        try {
            response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, Map.class);
            if (response.getStatusCode().isError()) {
                throw new AgentException("模型调用失败，状态码：" + response.getStatusCode());
            }
        } catch (RestClientException e) {
            throw new AgentException("调用AI服务异常", e);
        }

//        if (response.getStatusCode().isError()) throw new RuntimeException("模型调用失败");


        String text = getGeminiText(response);

        // todo 建造者模式优化
        LResponse lResponse = new LResponse();
        lResponse.setContext(text);
        lResponse.setUserId(lRequest.getUserId());
        lResponse.setChatId(lRequest.getChatId());
        lResponse.setMsgIndex(lRequest.getMsgIndex() + 1);

        return lResponse;
    }

    private HttpEntity<Map<String, Object>> sendGemini(LRequest lRequest, List<HistoryMessage> history, Prompt prompt, RoleCard roleCard) {
        // 1.user:prompt
        List<Map> contents = new ArrayList<>();

//        List<Map<String, String>> partsPrompt = List.of(Map.of("text", prompt == null ? "" : prompt.getText()));
        List<Map<String, String>> partsPrompt = List.of(Map.of("text", Optional.ofNullable(prompt).map(Prompt::getText).orElse("")));
        Map<String, Object> mapPrompt = new LinkedHashMap<>();
        mapPrompt.put("role", "user");
        mapPrompt.put("parts", partsPrompt);

        contents.add(mapPrompt);
        //2.model:角色卡开头
//        List<Map<String, String>> partsStartText = List.of(Map.of("test", roleCard == null ? "" : roleCard.getStartText()));
        List<Map<String, String>> partsStartText = List.of(Map.of("text", Optional.ofNullable(roleCard).map(RoleCard::getStartText).orElse("")));
        Map<String, Object> mapStartText = new LinkedHashMap<>();
        mapStartText.put("role", "model");
        mapStartText.put("parts", partsStartText);

        contents.add(mapStartText);
        //3.user-model:history
        for (HistoryMessage historyMessage : history) {
//            List<Map<String, String>> partsHistory = List.of(Map.of("text", historyMessage == null ? "" : historyMessage.getContext()));
            List<Map<String, String>> partsHistory = List.of(Map.of("text", historyMessage.getContext()));

            Map<String, Object> mapHistory = new LinkedHashMap<>();
            mapHistory.put("role", historyMessage.getRole());
            mapHistory.put("parts", partsHistory);

            contents.add(mapHistory);
        }
        //4.user:
        StringBuffer textLastText = new StringBuffer();
        //4.1键入消息
        textLastText.append(lRequest.getContext());
        //4.2角色卡设定
        textLastText.append(Optional.ofNullable(roleCard).map(RoleCard::getSettingText).orElse(""));
        //4.3worldBook
//        textLastText.append(worldBook.getNeedText());

        List<Map<String, String>> partsLastText = List.of(Map.of("text", textLastText.toString()));
        Map<String, Object> mapLastText = new LinkedHashMap<>();
        mapLastText.put("role", "user");
        mapLastText.put("parts", partsLastText);

        contents.add(mapLastText);
        //5稳定输出
        //5.1model
        String textPlusModel = "推荐以下面的指令&剧情继续：\n" + lRequest.getContext();
        List<Map<String, String>> partsPlusModel = List.of(Map.of("text", textPlusModel));
        Map<String, Object> mapPlusModel = new LinkedHashMap<>();
        mapPlusModel.put("role", "model");
        mapPlusModel.put("parts", partsPlusModel);

        contents.add(mapPlusModel);
        //5.2user
        String textPlusUser = "继续";
        List<Map<String, String>> partsPlusUser = List.of(Map.of("text", textPlusUser));
        Map<String, Object> mapPlusUser = new LinkedHashMap<>();
        mapPlusUser.put("role", "model");
        mapPlusUser.put("parts", partsPlusUser);

        contents.add(mapPlusUser);


        Map<String, Object> body = Map.of("contents", contents);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, httpHeaders);

        return entity;
    }

    private HttpEntity<Map<String, Object>> sendGeminiTextWithHistoryAndPrompt(LRequest lRequest, List<HistoryMessage> history, Prompt prompt) {
        List<Map> contents = new ArrayList<>();

        List<Map<String, String>> parts1 = List.of(Map.of("text", prompt.getText()));
        Map<String, Object> map1 = new HashMap<>();
        map1.put("role", "user");
        map1.put("parts", parts1);

        contents.add(map1);

        for (HistoryMessage historyMessage : history) {
            List<Map<String, String>> parts2 = List.of(Map.of("text", historyMessage.getContext()));

            Map<String, Object> map2 = new LinkedHashMap<>();
            map2.put("role", historyMessage.getRole());
            map2.put("parts", parts2);

            contents.add(map2);
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
