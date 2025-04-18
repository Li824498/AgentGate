package com.mylearn.agentgate.service;

import com.mylearn.agentgate.core.entity.LRequest;
import com.mylearn.agentgate.core.entity.LResponse;
import com.mylearn.agentgate.core.processor.AbstractChatProcessor;
import com.mylearn.agentgate.core.processor.ProcessorSelector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AgentService {
    @Autowired
    private ProcessorSelector processorSelector;

    @Autowired
    private RestTemplate restTemplate;

    public LResponse chatService(LRequest lRequest) {
        AbstractChatProcessor processor = processorSelector.selectModel(lRequest.getLMName());

        LResponse lResponse = processor.syncNonStreamChatProcess(lRequest, restTemplate);

        return lResponse;

    }

}
