package com.mylearn.agentgate.service;

import com.mylearn.agentgate.core.entity.HistoryRendered;
import com.mylearn.agentgate.mapper.HistoryRenderedMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistoryRenderedService {
    @Autowired
    private HistoryRenderedMapper historyRenderedMapper;


    public List<HistoryRendered> getByHistoryId(Long historyId) {
        return historyRenderedMapper.getByHistoryId(historyId);
    }

    public List<HistoryRendered> batchGetByHistoryIds(List<Long> historyIds) {
        return historyRenderedMapper.batchGetByHistoryIds(historyIds);
    }
}
