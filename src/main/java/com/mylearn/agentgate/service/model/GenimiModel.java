package com.mylearn.agentgate.service.model;

import com.mylearn.agentgate.annoation.ModelType;
import com.mylearn.agentgate.entity.ChatHistory;
import com.mylearn.agentgate.entity.LRequest;
import com.mylearn.agentgate.entity.LResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@ModelType("genimi")
public class GenimiModel implements LanguageModel{
    @Override
    public void sendLRequest(LRequest lRequest, ChatHistory chatHistory) {
        log.info("发送了genimi消息");

    }

    @Override
    public LResponse getLResponse(int uid_chat, int uid_position) {
        log.info("接受了genimi消息");
        return null;
    }
}
