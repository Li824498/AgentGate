package com.mylearn.agentgate.core.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WorldBook {
    private long id;
    private String userId;
    private String url;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
