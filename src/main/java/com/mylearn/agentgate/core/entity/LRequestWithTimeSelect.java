package com.mylearn.agentgate.core.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Data
public class LRequestWithTimeSelect {
    public String context;
    public String userId;
    public String chatId;
    public int promptId;
    public int roleCardId;
    public int msgIndex;
    public List<Long> worldBookIds;
    public List<String> renders;
    public String modelName;
    public String api;
    public LocalDateTime createTime;
}
