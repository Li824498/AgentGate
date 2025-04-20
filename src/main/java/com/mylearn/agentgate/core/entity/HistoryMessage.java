package com.mylearn.agentgate.core.entity;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HistoryMessage {
    public long id;
    public String userId;
    public String chatId;
    public int msgIndex;
    public String role;
    public String context;
    public LocalDateTime createTime;
}
