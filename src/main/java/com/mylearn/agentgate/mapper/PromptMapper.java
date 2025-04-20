package com.mylearn.agentgate.mapper;

import com.mylearn.agentgate.core.entity.Prompt;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PromptMapper {
    Prompt selectById(int id);
}
