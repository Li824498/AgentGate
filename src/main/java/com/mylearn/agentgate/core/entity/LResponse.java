package com.mylearn.agentgate.core.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class LResponse {
    public String inContext;
    public List<HistoryRendered> outContext;
    public String userId;
    public String chatId;
    public int msgIndex;
}
