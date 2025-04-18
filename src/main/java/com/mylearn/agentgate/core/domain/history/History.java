package com.mylearn.agentgate.core.domain.history;

import com.mylearn.agentgate.core.entity.LRequest;
import com.mylearn.agentgate.core.entity.LResponse;

public interface History {
    void process(LRequest lRequest, LResponse lResponse);
}
