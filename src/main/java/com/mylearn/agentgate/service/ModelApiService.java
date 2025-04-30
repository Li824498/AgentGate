package com.mylearn.agentgate.service;

import com.mylearn.agentgate.mapper.ModelApiMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class ModelApiService {

    public static final String EMB_MODEL_NAME = "text-embedding-3-small";
    @Autowired
    private ModelApiMapper modelApiMapper;

    public void insertModelAndApi(String model, String api) {
        modelApiMapper.insertModelAndApi(model, api);
    }

    public List<String> selectAllModels() {
        return modelApiMapper.selectAllModels();
    }

    public String getEmbApi() {
        List<String> embApiList = modelApiMapper.selectEmbApis();
        Random random = new Random();
        return embApiList.get(random.nextInt(embApiList.size()));
    }

    public String getChatApi(String model, String version) {
        List<String> chatApiList = modelApiMapper.selectChatApis(model);
        Random random = new Random();
        return chatApiList.get(random.nextInt(chatApiList.size()));
    }
}
