package com.mylearn.agentgate.core;

import com.mylearn.agentgate.core.entity.HistoryRendered;
import com.mylearn.agentgate.core.entity.LRequest;
import com.mylearn.agentgate.core.entity.LResponse;
import com.mylearn.agentgate.core.processor.AbstractChatProcessor;
import com.mylearn.agentgate.core.processor.ProcessorSelector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class Core {
    @Autowired
    private ProcessorSelector processorSelector;


    /**
     * 推理节点
     * @param lRequest
     * @return
     */
    public LResponse syncNonStreamChatProcess(LRequest lRequest) {
        AbstractChatProcessor processor = processorSelector.selectModel(lRequest.getModelName());

        LResponse lResponse = processor.syncNonStreamChatProcess(lRequest);

        return lResponse;
    }


    /**
     * 格式化节点
     * @param lRequest
     * @return
     */
    public List<HistoryRendered> RenderChatProcess(LRequest lRequest, LResponse lResponse) {
        AbstractChatProcessor processor = processorSelector.selectModel("gemini");

        List<HistoryRendered> historyRenderedList = processor.renderChatProcess(lRequest, lResponse);

        return historyRenderedList;
    }


    /**
     * 流式节点
     * @param lRequest
     * @return
     * @throws IOException
     */
    public Flux<LResponse> syncStreamChatProcess(LRequest lRequest) throws IOException {
        AbstractChatProcessor processor = processorSelector.selectModel("gemini");
        log.info(lRequest.toString());
        lRequest.setPromptId(1);
        lRequest.setRoleCardId(1);
        lRequest.setMsgIndex(15);
        lRequest.setWorldBookIds(List.of(2L));
        lRequest.setRenders(List.of("不使用渲染"));
        lRequest.setModelName("gemini");
        Flux<LResponse> lResponseFlux = processor.syncStreamChatProcess(lRequest);
        return lResponseFlux;
    }

}
