package com.mylearn.agentgate.service;

import com.mylearn.agentgate.core.entity.SuiteRender;
import com.mylearn.agentgate.dto.SuiteRenderDTO;
import com.mylearn.agentgate.mapper.SuiteRenderMapper;
import com.mylearn.agentgate.utils.SuiteIdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SuiteRenderService {
    @Autowired
    private SuiteRenderMapper suiteRenderMapper;

    @Autowired
    private SuiteIdUtils suiteIdUtils;


    public List<SuiteRenderDTO> getAllSuiteRenders() {
        List<SuiteRender> suiteRenders = suiteRenderMapper.getAllSuiteRenders();
        // todo 巨长的
        List<SuiteRenderDTO> suiteRenderDTOList = suiteRenders
                // 1.先分组
                .stream().collect(Collectors.groupingBy(suiteRender -> suiteRender.getId()))
                // 2.各组构建各自map并存入各自属性
                .entrySet().stream().map(longListEntry -> {
                    SuiteRenderDTO suiteRenderDTO = new SuiteRenderDTO();
                    suiteRenderDTO.setId(longListEntry.getKey());
                    suiteRenderDTO.setName(longListEntry.getValue().getFirst().getName());
                    suiteRenderDTO.setDescription(longListEntry.getValue().getFirst().getDescription());
                    // 3.删去多余字段得的简化的map
                    suiteRenderDTO.setRenderMap(longListEntry.getValue()
                            .stream().collect(Collectors.toMap(SuiteRender::getRenderId, SuiteRender::getRenderName)));
                    suiteRenderDTO.setCreateTime(longListEntry.getValue().getFirst().getCreateTime());
                    suiteRenderDTO.setUpdateTime(longListEntry.getValue().getFirst().getUpdateTime());
                    return suiteRenderDTO;
                }).collect(Collectors.toList());

        return suiteRenderDTOList;
    }

    public void save(SuiteRenderDTO suiteRenderDTO) {
        int id = suiteIdUtils.nextId();
        List<SuiteRender> suiteRenderList = suiteRenderDTO.getRenderMap().entrySet().stream().map(new Function<Map.Entry<Long, String>, SuiteRender>() {
            @Override
            public SuiteRender apply(Map.Entry<Long, String> longStringEntry) {
                SuiteRender suiteRender = new SuiteRender();
                suiteRender.setId(id);
                suiteRender.setName(suiteRenderDTO.getName());
                suiteRender.setDescription(suiteRenderDTO.getDescription());
                suiteRender.setRenderId(longStringEntry.getKey());
                suiteRender.setRenderName(longStringEntry.getValue());
                return suiteRender;
            }
        }).collect(Collectors.toList());
        suiteRenderMapper.batchInsert(suiteRenderList);
    }
}
