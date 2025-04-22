package com.mylearn.agentgate.core.processor;

import com.mylearn.agentgate.core.entity.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * 核心会话实体
 */
public abstract class AbstractChatProcessor {

    /**
     * 同步非流式传输模式
     * 新建聊天之后
     * @param lRequest
     * @return
     */
    // todo 事务bug
    @Transactional
    public LResponse syncNonStreamChatProcess(LRequest lRequest, RestTemplate restTemplate) {
        // todo 什么设计模式？？？？

        List<HistoryMessage> history = historyBefore(lRequest);
        chatMetaBefore(lRequest);
        Prompt prompt = prompt(lRequest);
        RoleCard roleCard = roleCard(lRequest);
        worldBook(lRequest);

        // todo 以后要一步架构怎么搞？
        LResponse lResponse = transferAi(lRequest, restTemplate, history, prompt, roleCard);

        historyAfter(lResponse);
        chatMetaAfter(lResponse);

        return lResponse;
    }

    abstract List<HistoryMessage> historyBefore(LRequest lRequest);
    abstract void historyAfter(LResponse lResponse);
    abstract void chatMetaBefore(LRequest lRequest);
    abstract void chatMetaAfter(LResponse lResponse);
    abstract Prompt prompt(LRequest lRequest);
    abstract RoleCard roleCard(LRequest lRequest);
    abstract void worldBook(LRequest lRequest);
    abstract LResponse transferAi(LRequest lRequest, RestTemplate restTemplate, List<HistoryMessage> history, Prompt prompt, RoleCard roleCard);
}
