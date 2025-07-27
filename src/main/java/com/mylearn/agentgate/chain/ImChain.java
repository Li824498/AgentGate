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
        LRequest lRequest = imCore.im2lRParser(imMessages);

        LResponse lResponse = core.syncNonStreamChatProcess(lRequest);
        imCore.historyCompensate(List.of(lRequest.getContext()), "user");

        List<String> messages = imCore.lR2imParser(lResponse, true);

        imCore.historyCompensate(messages, "model");

        return messages;
    }

}
