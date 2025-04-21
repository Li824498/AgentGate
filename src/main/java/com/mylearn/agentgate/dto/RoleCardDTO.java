package com.mylearn.agentgate.dto;

import lombok.Data;

@Data
public class RoleCardDTO {
    private String name;
    private String userId;
    private String description;
    private String avatarUrl;
    private String startText;
    private String settingText;
}
