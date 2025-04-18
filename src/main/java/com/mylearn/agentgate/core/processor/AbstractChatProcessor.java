package com.mylearn.agentgate.core.processor;

import com.mylearn.agentgate.core.entity.LRequest;
import com.mylearn.agentgate.core.entity.LResponse;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public abstract class AbstractChatProcessor {

    /**
     * 同步非流式传输模式
     * @param lRequest
     * @return
     */
    public final LResponse syncNonStreamChatProcess(LRequest lRequest, RestTemplate restTemplate) {
        // todo 什么设计模式？？？？

        history(lRequest);
        prompt(lRequest);
        worldBook(lRequest);

        // todo 以后要一步架构怎么搞？
        LResponse lResponse = transferAi(lRequest, restTemplate);

        // todo build建造者优化

        return lResponse;
    }

    abstract void history(LRequest lRequest);
    abstract void prompt(LRequest lRequest);
    abstract void worldBook(LRequest lRequest);
    abstract LResponse transferAi(LRequest lRequest, RestTemplate restTemplate);
}
