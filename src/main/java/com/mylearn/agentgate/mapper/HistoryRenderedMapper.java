package com.mylearn.agentgate.mapper;

import com.mylearn.agentgate.core.entity.HistoryRendered;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HistoryRenderedMapper {

    void batchInsert(@Param("historyRenderedList") List<HistoryRendered> historyRenderedList);

    List<HistoryRendered> getByHistoryId(Long historyId);

    List<HistoryRendered> batchGetByHistoryIds(List<Long> historyIds);
}
