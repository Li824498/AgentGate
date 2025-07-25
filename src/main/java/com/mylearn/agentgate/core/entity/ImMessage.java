package com.mylearn.agentgate.core.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ImMessage {
    public String text;
    public LocalDateTime time;
}
