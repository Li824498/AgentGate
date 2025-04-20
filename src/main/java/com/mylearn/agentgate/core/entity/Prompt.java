package com.mylearn.agentgate.core.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Prompt {
    public int id;
    public String name;
    public String userId;
    public String text;
    public LocalDateTime createTime;
    public LocalDateTime updateTime;
}
