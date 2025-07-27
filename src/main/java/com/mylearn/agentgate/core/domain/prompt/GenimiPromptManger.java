package com.mylearn.agentgate.core.domain.prompt;

import com.mylearn.agentgate.core.entity.LRequest;
import com.mylearn.agentgate.core.entity.Prompt;
import com.mylearn.agentgate.mapper.PromptMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GenimiPromptManger implements PromptManager {
    @Autowired
    private PromptMapper promptMapper;

    @Override
    public Prompt process(LRequest lRequest) {
        Prompt prompt = promptMapper.selectById(lRequest.getPromptId());
        log.info("根据id=" + lRequest.getPromptId() + ",查到了prompt:" + prompt.getPrompt());
        return prompt;
    }
}
