package com.mylearn.agentgate.mapper;

import com.mylearn.agentgate.core.entity.Render;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RenderMapper {
    List<Render> selectByNames(List<String> renders);
}
