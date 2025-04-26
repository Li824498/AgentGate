package com.mylearn.agentgate.service;

import com.mylearn.agentgate.chain.Chain;
import com.mylearn.agentgate.core.entity.LRequest;
import com.mylearn.agentgate.core.entity.LResponse;
import com.mylearn.agentgate.core.processor.AbstractChatProcessor;
import com.mylearn.agentgate.core.processor.ProcessorSelector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;

import java.io.IOException;

@Service
public class AgentService {

    @Autowired
    private Chain chain;



    public LResponse syncNonStreamChatService(LRequest lRequest) {
        return chain.RenderNonChain(lRequest);

    }

    public Flux<LResponse> syncStreamChatService(LRequest lRequest) throws IOException {
        return chain.RenderNonStreamChain(lRequest);
    }
}
