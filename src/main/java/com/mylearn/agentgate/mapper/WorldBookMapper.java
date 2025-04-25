package com.mylearn.agentgate.mapper;

import com.mylearn.agentgate.core.entity.WorldBook;
import com.mylearn.agentgate.dto.WorldBookDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WorldBookMapper {
    public List<WorldBook> selectByIds(List<Long> worldBookIds);

    void insert(WorldBook worldBook);

    void updateStatusByUrl(String url);

    List<WorldBookDTO> selectAllWorldBooks();
}
