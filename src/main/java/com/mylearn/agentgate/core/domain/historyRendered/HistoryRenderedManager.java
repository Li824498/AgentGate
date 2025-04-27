package com.mylearn.agentgate.core.domain.historyRendered;

import com.mylearn.agentgate.core.entity.HistoryRendered;
import com.mylearn.agentgate.core.entity.LRequest;

import java.util.List;

public interface HistoryRenderedManager {
    void processAfters(LRequest lRequest, List<HistoryRendered> historyRenderedList);
}
