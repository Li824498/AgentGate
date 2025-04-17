package com.mylearn.agentgate.service.model;

import com.mylearn.agentgate.annoation.ModelType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ModelSelector {

    private final Map<String, LanguageModel> languageModelMap = new HashMap<>();
    private final LanguageModel defaultModel;

    @Autowired
    public ModelSelector(List<LanguageModel> languageModelList, GenimiModel defaultModel) {
        this.defaultModel = defaultModel;
        for (LanguageModel languageModel : languageModelList) {
            ModelType annotation = languageModel.getClass().getAnnotation(ModelType.class);
            String languageModelName = annotation.value();
            languageModelMap.put(languageModelName, languageModel);
        }
    }

    public LanguageModel selectModel(String name) {
        return languageModelMap.getOrDefault(name, defaultModel);
    }
}
