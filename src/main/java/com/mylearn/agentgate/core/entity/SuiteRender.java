package com.mylearn.agentgate.core.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SuiteRender {
    private int idx;
    private int id;
    private String name;
    private String description;
    private long renderId;
    private String renderName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
