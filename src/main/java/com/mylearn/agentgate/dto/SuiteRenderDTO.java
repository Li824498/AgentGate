package com.mylearn.agentgate.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class SuiteRenderDTO {
    private int id;
    private String name;
    private String description;
    private Map<Long, String> renderMap;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
