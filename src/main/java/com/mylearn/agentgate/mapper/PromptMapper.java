package com.mylearn.agentgate.mapper;

import com.mylearn.agentgate.core.entity.Prompt;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PromptMapper {
    Prompt selectById(int id);

    void save(String name, String userId, String prompt);

    List<Prompt> selectAllPrompts();

}
