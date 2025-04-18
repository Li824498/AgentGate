package com.mylearn.agentgate.service.model;

import com.mylearn.agentgate.core.entity.ChatHistory;
import com.mylearn.agentgate.core.entity.LRequest;
import com.mylearn.agentgate.core.entity.LResponse;
import org.springframework.web.client.RestTemplate;

public interface LanguageModel {
    LResponse sendLRequest(LRequest lRequest, ChatHistory chatHistory, RestTemplate restTemplate);
    LResponse getLResponse(int uid_chat, int uid_position);
}
