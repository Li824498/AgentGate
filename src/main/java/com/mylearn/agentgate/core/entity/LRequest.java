package com.mylearn.agentgate.core.entity;

import lombok.Data;

@Data
public class LRequest {
    public String text;
    public String userId;
    public String chatId;
    public int msgIndex;
    public String lMName;
    public String api;

}
