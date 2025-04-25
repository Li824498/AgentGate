package com.mylearn.agentgate.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WorldBookDTO {
    private long id;
    private String name;
    private String userId;
    private String description;
    private String url;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
