package com.mylearn.agentgate.service;

import com.mylearn.agentgate.entity.LRequest;
import com.mylearn.agentgate.entity.LResponse;
import com.mylearn.agentgate.service.model.LanguageModel;
import com.mylearn.agentgate.service.model.ModelSelector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AgentService {
    @Autowired
    private ModelSelector modelSelector;

    public LResponse chatService(LRequest lRequest) {
        LanguageModel model = modelSelector.selectModel(lRequest.getLMName());

        // todo history处理
        model.sendLRequest(lRequest, null);

        return model.getLResponse(lRequest.getUid_chat(), lRequest.getUid_position());
    }

}
