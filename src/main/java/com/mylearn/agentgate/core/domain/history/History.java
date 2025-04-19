package com.mylearn.agentgate.core.domain.history;

import com.mylearn.agentgate.core.entity.HistoryMessage;
import com.mylearn.agentgate.core.entity.LRequest;

import java.util.List;

public interface History {
    List<HistoryMessage> process(LRequest lRequest);
}
