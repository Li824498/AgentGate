package com.mylearn.agentgate.core.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mylearn.agentgate.core.domain.history.GeminiHistoryManager;
import com.mylearn.agentgate.core.entity.ImMessage;
import com.mylearn.agentgate.core.entity.LRequest;
import com.mylearn.agentgate.core.entity.LResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class ImProcessor {
    @Autowired
    private GeminiHistoryManager historyManager;

    public List<String> lR2imParser(LResponse lResponse, boolean generateTime) {
        String json = lResponse.getInContext();

        ObjectMapper objectMapper = new ObjectMapper();
        List<String> messages = new ArrayList<>();
        try {
            messages = objectMapper.readValue(json, List.class);
        } catch (JsonProcessingException e) {
            // todo 鲁棒性太差看看怎么解决
            throw new RuntimeException(e);
        }

        return messages;
    }


    /**
     * 主动输入消息转换为请求
     * 结构：日期+消息
     * @param imMessages
     * @return
     */
    public LRequest im2lRParser(List<String> imMessages) {
        LRequest lRequest = new LRequest();
        //todo 肯定不能用geminiProcessor，架构太烂了，要改，但是先暂时用着
        StringBuilder stringBuilder = new StringBuilder();
        for (String imMessage : imMessages) {
            stringBuilder.append(LocalDateTime.now() + "  " + imMessage);
        }
        String input = stringBuilder.toString();

        lRequest.setContext(input);
        lRequest.setUserId("Takesth");
        lRequest.setChatId("hikariChat");
        lRequest.setPromptId(0);
        lRequest.setRoleCardId(0);
        lRequest.setMsgIndex(0);
        lRequest.setWorldBookIds(Collections.emptyList());
        lRequest.setRenders(Collections.emptyList());
        lRequest.setModelName("gemini");
        lRequest.setApi("api");

        return lRequest;
    }

    public void historyCompensate(List<String> messages, String role) {
        //删除最后一条
        int count = historyManager.imHistoryCompensate();
        //加入正确的最后几条
//        historyManager.imHistoryAfter(messages);
        historyManager.imHistoryInsert(count, messages, role);
    }
}
