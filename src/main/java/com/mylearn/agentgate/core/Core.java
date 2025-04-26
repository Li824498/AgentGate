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


    /**
     * 推理节点
     * @param lRequest
     * @return
     */
    public String syncNonStreamChatProcess(LRequest lRequest) {
        AbstractChatProcessor processor = processorSelector.selectModel(lRequest.getModelName());

        LResponse lResponse = processor.syncNonStreamChatProcess(lRequest);

        return lResponse.getInContext();
    }


    /**
     * 格式化节点
     * @param lRequest
     * @return
     */
    public String RenderChatProcess(LRequest lRequest, String inContext) {
        AbstractChatProcessor processor = processorSelector.selectModel("gemini");

        String outContext = processor.renderChatProcess(lRequest, inContext);

        return outContext;
    }


    /**
     * 流式节点
     * @param lRequest
     * @return
     * @throws IOException
     */
    public Flux<LResponse> syncStreamChatProcess(LRequest lRequest) throws IOException {
        AbstractChatProcessor processor = processorSelector.selectModel(lRequest.getModelName());

        Flux<LResponse> lResponseFlux = processor.syncStreamChatProcess(lRequest);
        return lResponseFlux;
    }

}
