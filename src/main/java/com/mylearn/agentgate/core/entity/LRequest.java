package com.mylearn.agentgate.core.entity;

import lombok.Data;

import java.util.List;

@Data
public class LRequest {
    public String context;
    public String userId;
    public String chatId;
    public int promptId;
    public int roleCardId;
    public int msgIndex;
    public List<Long> worldBookIds;
    public String modelName;
    public String api;

}
