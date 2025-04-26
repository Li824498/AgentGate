package com.mylearn.agentgate.core.domain.render;

import com.mylearn.agentgate.core.entity.LRequest;
import com.mylearn.agentgate.core.entity.Render;
import com.mylearn.agentgate.mapper.RenderMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class GeminiRenderManager implements RenderManager{
    @Autowired
    private RenderMapper renderMapper;

    @Override
    public List<Render> process(LRequest lRequest, String inContext) {
        List<Render> renders = renderMapper.selectByNames(lRequest.getRenders());
        return renders;
    }
}
