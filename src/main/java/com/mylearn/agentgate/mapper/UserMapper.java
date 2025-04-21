package com.mylearn.agentgate.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    String selectById(String userId);
}
