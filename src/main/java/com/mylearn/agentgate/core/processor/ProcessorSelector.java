package com.mylearn.agentgate.core.processor;

import com.mylearn.agentgate.annoation.ModelType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ProcessorSelector {
    private final Map<String, AbstractChatProcessor> processorMap = new HashMap<>();

    @Autowired
    private GeminiProcessor defaultProcessor;

    @Autowired
    public ProcessorSelector(List<AbstractChatProcessor> processorList) {
        for (AbstractChatProcessor processor : processorList) {
            ModelType annotation = processor.getClass().getAnnotation(ModelType.class);
            String modelName = annotation.value();
            processorMap.put(modelName, processor);
        }
    }

    public AbstractChatProcessor selectModel(String modelName) {
        AbstractChatProcessor processor = processorMap.get(modelName);
        return processor;
    }

}
