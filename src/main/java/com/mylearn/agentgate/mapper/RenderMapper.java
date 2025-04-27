package com.mylearn.agentgate.mapper;

import com.mylearn.agentgate.core.entity.Render;
import com.mylearn.agentgate.dto.RenderDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RenderMapper {
    List<Render> selectByNames(@Param("renders") List<String> renders);

    List<Render> selectAllRenders();

    void insert(RenderDTO renderDTO);
}
