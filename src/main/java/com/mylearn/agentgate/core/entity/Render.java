package com.mylearn.agentgate.core.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Render {
    private int id;
    private String name;
    private String description;
    private String text;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
