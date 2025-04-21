package com.mylearn.agentgate.core.entity;

import lombok.Data;

@Data
public class LRequest {
    public String context;
    public String userId;
    public String chatId;
    public int promptId;
    public int roleCardId;
    public int msgIndex;
    public String modelName;
    public String api;

}
