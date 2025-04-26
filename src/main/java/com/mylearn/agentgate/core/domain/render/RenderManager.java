package com.mylearn.agentgate.core.domain.render;

import com.mylearn.agentgate.core.entity.LRequest;
import com.mylearn.agentgate.core.entity.Render;

import java.util.List;

public interface RenderManager {
    public List<Render> process(LRequest lRequest, String inContext);
}
