package com.mylearn.agentgate.core.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMeta {
    long id;
    String chatId;
    String userId;
    String lastHistory;
    LocalDateTime createTime;
    LocalDateTime updateTime;
}
