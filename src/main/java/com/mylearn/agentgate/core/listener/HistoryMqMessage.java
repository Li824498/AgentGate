package com.mylearn.agentgate.core.listener;

import com.mylearn.agentgate.core.entity.LRequest;
import lombok.Data;

@Data
public class HistoryMqMessage {
    private String bucketName;
    private LRequest lRequest;
}
