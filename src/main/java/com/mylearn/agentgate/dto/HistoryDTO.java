package com.mylearn.agentgate.dto;

import com.mylearn.agentgate.core.entity.HistoryMessage;
import com.mylearn.agentgate.core.entity.HistoryRendered;
import lombok.Data;

import java.util.List;

@Data
public class HistoryDTO {
    private HistoryMessage historyMessage;
    private List<HistoryRendered> historyRenderedList;
}
