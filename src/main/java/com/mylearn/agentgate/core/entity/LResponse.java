package com.mylearn.agentgate.core.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LResponse {
    public String inContext;
    public String outContext;
    public String userId;
    public String chatId;
    public int msgIndex;
}
