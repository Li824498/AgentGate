package com.mylearn.agentgate.core.domain.history;

import com.mylearn.agentgate.core.entity.HistoryMessage;
import com.mylearn.agentgate.core.entity.LRequest;
import com.mylearn.agentgate.core.entity.LResponse;

import java.util.List;

public interface HistoryManager {
    List<HistoryMessage> processBefore(LRequest lRequest);

    void processAfter(LResponse lResponse);

    void chatMetaProcessBefore(LRequest lRequest);

    void chatMetaProcessAfter(LRequest lRequest, LResponse lResponse);
}
