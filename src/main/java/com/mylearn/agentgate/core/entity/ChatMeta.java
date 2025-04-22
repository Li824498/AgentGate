package com.mylearn.agentgate.core.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMeta {
    long id;
    String chatId;
    String userId;
    String lastHistory;
    int roleCardId;
    String roleCardName;
    int msgNum;
    LocalDateTime createTime;
    LocalDateTime updateTime;
}
