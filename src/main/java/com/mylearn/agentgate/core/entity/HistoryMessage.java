package com.mylearn.agentgate.core.entity;


import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString
public class HistoryMessage {
    public long id;
    public String userId;
    public String chatId;
    public int msgIndex;
    public String role;
    public String context;
    public LocalDateTime createTime;
    public LocalDateTime updateTime;
}
