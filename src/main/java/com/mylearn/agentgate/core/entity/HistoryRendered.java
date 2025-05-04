package com.mylearn.agentgate.core.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HistoryRendered {
    private long id;
    private String userId;
    private long msgIndex;
    private String renderType;
    private String outContext;
    private long historyId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
