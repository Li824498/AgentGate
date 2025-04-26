package com.mylearn.agentgate.core;

import com.mylearn.agentgate.core.entity.LRequest;
import com.mylearn.agentgate.core.entity.LResponse;
import com.mylearn.agentgate.core.processor.AbstractChatProcessor;
import com.mylearn.agentgate.core.processor.ProcessorSelector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.io.IOException;

@Component
public class Core {
    @Autowired
    private ProcessorSelector processorSelector;


    public LResponse syncNonStreamChatProcess(LRequest lRequest) {
        AbstractChatProcessor processor = processorSelector.selectModel(lRequest.getModelName());

        LResponse lResponse = processor.syncNonStreamChatProcess(lRequest);

        return lResponse;
    }

    public Flux<LResponse> syncStreamChatProcess(LRequest lRequest) throws IOException {
        AbstractChatProcessor processor = processorSelector.selectModel(lRequest.getModelName());

        Flux<LResponse> lResponseFlux = processor.syncStreamChatProcess(lRequest);
        return lResponseFlux;
    }

}
