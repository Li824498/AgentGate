package com.mylearn.agentgate.core.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RoleCard {
    public int id;
    public String name;
    public String userId;
    public String description;
    public String avatarUrl;
    public String startText;
    public String settingText;
    public LocalDateTime createTime;
    public LocalDateTime updateTime;
}
