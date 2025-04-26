package com.mylearn.agentgate.chain;

import com.mylearn.agentgate.core.Core;
import com.mylearn.agentgate.core.entity.LRequest;
import com.mylearn.agentgate.core.entity.LResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.io.IOException;

@Component
public class Chain {
    @Autowired
    private Core core;

    public LResponse RenderNonChain(LRequest lRequest) {
        return core.syncNonStreamChatProcess(lRequest);
    }

    public Flux<LResponse> RenderNonStreamChain(LRequest lRequest) throws IOException {
        return core.syncStreamChatProcess(lRequest);
    }
}
