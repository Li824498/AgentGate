package com.mylearn.agentgate.service;

import com.mylearn.agentgate.mapper.ModelApiMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModelApiService {

    @Autowired
    private ModelApiMapper modelApiMapper;

    public void insertModelAndApi(String model, String api) {
        modelApiMapper.insertModelAndApi(model, api);
    }

    public List<String> selectAllModels() {
        return modelApiMapper.selectAllModels();
    }
}
