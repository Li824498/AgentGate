package com.mylearn.agentgate.mapper;

import com.mylearn.agentgate.core.entity.SuiteRender;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SuiteRenderMapper {
    /**
     * 迫真批量查询
     * @return
     */
    List<SuiteRender> getAllSuiteRenders();

    /**
     * 这个是真的批量插入了
     * @param suiteRenderList
     */
    void batchInsert(@Param("suiteRenderList") List<SuiteRender> suiteRenderList);
}
