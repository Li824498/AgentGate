package com.mylearn.agentgate.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ModelApiMapper {

    void insertModelAndApi(String model, String api);

    List<String> selectAllModels();
}
