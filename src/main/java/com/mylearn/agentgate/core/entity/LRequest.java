package com.mylearn.agentgate.core.entity;

import lombok.Data;

@Data
public class LRequest {
    public String text;
    public int uid_chat;
    public int uid_position;
    public String lMName;
    public String api;

}
