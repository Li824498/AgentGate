package com.mylearn.agentgate.core.processor;

import com.mylearn.agentgate.core.entity.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public abstract class AbstractChatProcessor {

    /**
     * 同步非流式传输模式
     * @param lRequest
     * @return
     */
    public final LResponse syncNonStreamChatProcess(LRequest lRequest, RestTemplate restTemplate) {
        // todo 什么设计模式？？？？

        List<HistoryMessage> history = historyBefore(lRequest);
        Prompt prompt = prompt(lRequest);
        RoleCard roleCard = roleCard(lRequest);
        worldBook(lRequest);

        // todo 以后要一步架构怎么搞？
        LResponse lResponse = transferAi(lRequest, restTemplate, history, prompt, roleCard);

        historyAfter(lResponse);

        return lResponse;
    }

    abstract List<HistoryMessage> historyBefore(LRequest lRequest);
    abstract void historyAfter(LResponse lResponse);
    abstract Prompt prompt(LRequest lRequest);
    abstract RoleCard roleCard(LRequest lRequest);
    abstract void worldBook(LRequest lRequest);
    abstract LResponse transferAi(LRequest lRequest, RestTemplate restTemplate, List<HistoryMessage> history, Prompt prompt, RoleCard roleCard);
}
