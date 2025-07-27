package com.mylearn.agentgate.core.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mylearn.agentgate.core.domain.history.GeminiHistoryManager;
import com.mylearn.agentgate.core.entity.ImMessage;
import com.mylearn.agentgate.core.entity.LRequest;
import com.mylearn.agentgate.core.entity.LResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class ImProcessor {
    @Autowired
    private GeminiHistoryManager historyManager;

    public List<String> lR2imParser(LResponse lResponse, boolean generateTime) {
        log.info("stage 4-convert: process start!");
        String json = lResponse.getInContext();
        log.info("stage 4-convert: 要转换的信息: " + json);

        ObjectMapper objectMapper = new ObjectMapper();
        List<String> messages = new ArrayList<>();
        try {
            messages = objectMapper.readValue(json, List.class);
            log.info("stage 4-convert: 转换结果: " + messages.toString());
        } catch (JsonProcessingException e) {
            // todo 鲁棒性太差看看怎么解决
            throw new RuntimeException(e);
        }

        log.info("stage 4-convert: process completed!");
        return messages;
    }


    /**
     * 主动输入消息转换为请求
     * 结构：日期+消息
     * @param imMessages
     * @return
     */
    public LRequest im2lRParser(List<String> imMessages) {
        log.info("stage 1-convert: start process");
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

        log.info("stage 1-convert: process result: " + lRequest.toString());

        log.info("stage 1-convert: process completed!");

        return lRequest;
    }

    public void historyCompensate(List<String> messages, String role) {
        log.info("stage 3-compensate-user: process start!");
        //删除最后一条
        int count = historyManager.imHistoryCompensate();
        log.info("stage 3-compensate-user: " + "目前总共有" + count + "历史记录," + "删除了msgIndex=" + count + "的数据");
        //加入正确的最后几条
//        historyManager.imHistoryAfter(messages);
        historyManager.imHistoryInsert(count, messages, role);
        log.info("stage 3-compensate-user: " + "插入了正确的最后几条数据,目前应该只是一条:" + role + ":" + messages.toString());

        log.info("stage 3-compensate-user: process completed!");
    }
}
