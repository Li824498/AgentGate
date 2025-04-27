package com.mylearn.agentgate.core.domain.historyRendered;

import com.mylearn.agentgate.core.entity.HistoryRendered;
import com.mylearn.agentgate.core.entity.LRequest;
import com.mylearn.agentgate.mapper.HistoryRenderedMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GeminiHistoryRenderedManger implements HistoryRenderedManager{
    @Autowired
    private HistoryRenderedMapper renderedMapper;

    @Override
    public void processAfters(LRequest lRequest, List<HistoryRendered> historyRenderedList) {
        renderedMapper.batchInsert(historyRenderedList);
    }
}
