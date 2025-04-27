package com.mylearn.agentgate.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RenderDTO {
    private int id;
    private String name;
    private String description;
    private String text;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
