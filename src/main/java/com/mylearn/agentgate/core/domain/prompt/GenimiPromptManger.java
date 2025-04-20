package com.mylearn.agentgate.core.domain.prompt;

import com.mylearn.agentgate.core.entity.LRequest;
import com.mylearn.agentgate.core.entity.LResponse;
import com.mylearn.agentgate.core.entity.Prompt;
import com.mylearn.agentgate.mapper.PromptMapper;
import org.springframework.beans.factory.annotation.Autowired;

public class GenimiPromptManger implements PromptManager {
    @Autowired
    private PromptMapper promptMapper;

    @Override
    public Prompt process(LRequest lRequest) {
        Prompt prompt = promptMapper.selectById(lRequest.getPromptId());
        return prompt;
    }
}
