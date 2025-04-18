package com.mylearn.agentgate.core.domain.prompt;

import com.mylearn.agentgate.core.entity.LRequest;
import com.mylearn.agentgate.core.entity.LResponse;

public interface Prompt {
    void process(LRequest lRequest, LResponse lResponse);
}
