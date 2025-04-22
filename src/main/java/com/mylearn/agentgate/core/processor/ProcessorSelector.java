package com.mylearn.agentgate.core.processor;

import com.mylearn.agentgate.annoation.ModelType;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
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
            // todo cglib动态代理byd
/*            ModelType annotation = processor.getClass().getAnnotation(ModelType.class);
            Class<? extends AbstractChatProcessor> aClass = processor.getClass();
            // test1
            for (Annotation aClassAnnotation : aClass.getAnnotations()) {
                System.out.println(aClassAnnotation);
            }
            //test2
            for (Annotation annotation1 : new GeminiProcessor().getClass().getAnnotations()) {
                System.out.println(annotation1);
            }
            //test3
            AbstractChatProcessor processor2 = new GeminiProcessor();
            for (Annotation annotation1 : processor2.getClass().getAnnotations()) {
                System.out.println(annotation1);
            }*/

            Class<? extends AbstractChatProcessor> aClass = processor.getClass();
            for (Class<?> anInterface : aClass.getInterfaces()) {
                System.out.println(anInterface.getName());
            }

            ModelType annotation = AnnotationUtils.findAnnotation(aClass, ModelType.class);
            String modelName = annotation.value();
            processorMap.put(modelName, processor);
        }
    }

    public AbstractChatProcessor selectModel(String modelName) {
        AbstractChatProcessor processor = processorMap.get(modelName);
        return processor;
    }

}
