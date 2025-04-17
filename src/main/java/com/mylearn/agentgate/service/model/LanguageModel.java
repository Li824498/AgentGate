package com.mylearn.agentgate.service.model;

import com.mylearn.agentgate.entity.ChatHistory;
import com.mylearn.agentgate.entity.LRequest;
import com.mylearn.agentgate.entity.LResponse;

public interface LanguageModel {
    void sendLRequest(LRequest lRequest, ChatHistory chatHistory);
    LResponse getLResponse(int uid_chat, int uid_position);
}
