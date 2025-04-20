package com.mylearn.agentgate.core.domain.prompt;

import com.mylearn.agentgate.core.entity.LRequest;
import com.mylearn.agentgate.core.entity.LResponse;
import com.mylearn.agentgate.core.entity.Prompt;

public interface PromptManager {
    Prompt process(LRequest lRequest);
}
