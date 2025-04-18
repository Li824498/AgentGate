package com.mylearn.agentgate.core.processor;

import com.mylearn.agentgate.core.entity.LRequest;
import com.mylearn.agentgate.core.entity.LResponse;

public abstract class AbstractChatProcessor {

    public final LResponse chatProcess(LRequest lRequest) {
        // todo 什么设计模式？？？？
        LResponse lResponse = new LResponse();
        history(lRequest, lResponse);
        prompt(lRequest, lResponse);
        worldBook(lRequest, lResponse);
        return lResponse;
    }

    abstract void history(LRequest lRequest, LResponse lResponse);
    abstract void prompt(LRequest lRequest, LResponse lResponse);
    abstract void worldBook(LRequest lRequest, LResponse lResponse);
}
