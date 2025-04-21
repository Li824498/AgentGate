package com.mylearn.agentgate.service;

import com.mylearn.agentgate.core.entity.Prompt;
import com.mylearn.agentgate.mapper.PromptMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PromptService {
    @Autowired
    private PromptMapper promptMapper;

    public void save(String name, String userId, String prompt) {
        promptMapper.save(name, userId, prompt);
    }

    public List<Prompt> selectAllPrompts() {
        return promptMapper.selectAllPrompts();
    }
}
