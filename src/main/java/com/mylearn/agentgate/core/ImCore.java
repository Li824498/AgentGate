package com.mylearn.agentgate.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mylearn.agentgate.core.entity.ImMessage;
import com.mylearn.agentgate.core.entity.LRequest;
import com.mylearn.agentgate.core.entity.LResponse;
import com.mylearn.agentgate.core.processor.ImProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class ImCore {
    @Autowired
    public ImProcessor imProcessor;

    public List<String> lR2imParser(LResponse lResponse, boolean generateTime) {
        return imProcessor.lR2imParser(lResponse, generateTime);
    }


    /**
     * 主动输入消息转换为请求
     * 结构：日期+消息
     * @param imMessages
     * @return
     */
    public LRequest im2lRParser(List<ImMessage> imMessages) {
        return imProcessor.im2lRParser(imMessages);
    }

    public void historyCompensate(List<String> messages, String role) {
        imProcessor.historyCompensate(messages, role);
    }
}
