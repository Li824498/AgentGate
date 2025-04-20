package com.mylearn.agentgate.core.entity;

import lombok.Data;

@Data
public class LResponse {
    public String conText;
    public String userId;
    public String chatId;
    public int msgIndex;
}
