package com.mylearn.agentgate.chain;

import com.mylearn.agentgate.core.Core;
import com.mylearn.agentgate.core.entity.LRequest;
import com.mylearn.agentgate.core.entity.LResponse;
import com.mylearn.agentgate.utils.UserIdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.io.IOException;

@Component
public class Chain {
    @Autowired
    private Core core;

    public LResponse RenderNonChain(LRequest lRequest) {
        String inContext = core.syncNonStreamChatProcess(lRequest);

        LResponse lResponse = new LResponse();
        lResponse.setMsgIndex(lRequest.getMsgIndex() + 1);
        lResponse.setUserId(UserIdUtils.getUserId());
        lResponse.setChatId(lResponse.getChatId());
        lResponse.setInContext(inContext);

        return lResponse;
    }

    public Flux<LResponse> RenderNonStreamChain(LRequest lRequest) throws IOException {
        return core.syncStreamChatProcess(lRequest);
    }

    public LResponse RenderChain(LRequest lRequest) {
        String inContext = core.syncNonStreamChatProcess(lRequest);

        String outContext = core.RenderChatProcess(lRequest, inContext);
    }
}
