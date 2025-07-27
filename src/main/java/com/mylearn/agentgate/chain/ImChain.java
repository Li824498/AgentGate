package com.mylearn.agentgate.chain;

import com.mylearn.agentgate.core.Core;
import com.mylearn.agentgate.core.ImCore;
import com.mylearn.agentgate.core.entity.ImMessage;
import com.mylearn.agentgate.core.entity.LRequest;
import com.mylearn.agentgate.core.entity.LResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class ImChain {
    @Autowired
    private Core core;

    @Autowired
    private ImCore imCore;


    public List<String> syncImChatChain(List<String> imMessages) {
        int count = imCore.count();

        LRequest lRequest = imCore.im2lRParser(imMessages, count);

        log.info("stage 2-chat: process start!");
        LResponse lResponse = core.syncNonStreamChatProcess(lRequest);
        log.info("stage 2-chat: process completed!");

//        imCore.historyCompensate(List.of(lRequest.getContext()), "user");

        List<String> messages = imCore.lR2imParser(lResponse, true);

//        imCore.historyCompensate(messages, "model");

        // todo 先把补偿给关了，要不然笨笨的，哈哈哈
//        imCore.historyCompensate(List.of(lRequest.getContext()), messages);
//        imCore.historyCompensateOnlyMsgIndexAndId();

        return messages;
    }

}
