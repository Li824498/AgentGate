package com.mylearn.agentgate.core.domain.worldBook;

import com.mylearn.agentgate.core.entity.LRequest;
import com.mylearn.agentgate.core.entity.LResponse;

public interface WorldBook {
    void Process(LRequest lRequest, LResponse lResponse);
}
