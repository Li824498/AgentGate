package com.mylearn.agentgate.service;

import com.mylearn.agentgate.core.entity.Render;
import com.mylearn.agentgate.dto.RenderDTO;
import com.mylearn.agentgate.mapper.RenderMapper;
import com.mylearn.agentgate.utils.BeanCollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RenderService {
    @Autowired
    private RenderMapper renderMapper;

    public List<RenderDTO> selectAllRenders() {
        List<Render> renders = renderMapper.selectAllRenders();
/*        List<RenderDTO> renderDTOS = renders.stream().map(new Function<Render, RenderDTO>() {
            @Override
            public RenderDTO apply(Render render) {
                RenderDTO renderDTO = new RenderDTO();
                BeanUtils.copyProperties(render, renderDTO);
                return renderDTO;
            }
        }).collect(Collectors.toList());*/
        List<RenderDTO> renderDTOS = BeanCollectionUtils.copyListProperties(renders, RenderDTO.class);
        return renderDTOS;
    }

    public void insert(RenderDTO renderDTO) {
        renderMapper.insert(renderDTO);
    }
}
