package com.mylearn.agentgate.core.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RoleCard {
    public int id;
    public String name;
    public int userId;
    public String startText;
    public String settingText;
    public LocalDateTime createTime;
    public LocalDateTime updateTime;
}
