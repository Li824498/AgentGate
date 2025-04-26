package com.mylearn.agentgate.service;

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
    private ProcessorSelector processorSelector;


    @Autowired
    private RestTemplate restTemplate;

    public LResponse syncNonStreamChatService(LRequest lRequest) {
        AbstractChatProcessor processor = processorSelector.selectModel(lRequest.getModelName());

        LResponse lResponse = processor.syncNonStreamChatProcess(lRequest, restTemplate);

        return lResponse;

    }

    public Flux<LResponse> syncStreamChatService(LRequest lRequest) throws IOException {
        AbstractChatProcessor processor = processorSelector.selectModel(lRequest.getModelName());

        Flux<LResponse> lResponseFlux = processor.syncStreamChatProcess(lRequest);
        return lResponseFlux;
    }
}
