package com.mylearn.agentgate.utils;

import org.springframework.beans.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BeanCollectionUtils {
    public static final <T,S> List<T> copyListProperties(List<S> sourceList, Class<T> targetClass) {
        List<T> collectList = sourceList.stream().map(new Function<S, T>() {
            @Override
            public T apply(S source) {
                T target = null;
                try {
                    target = targetClass.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException("copy fail!", e);
                }
                BeanUtils.copyProperties(source, target);
                return target;
            }
        }).collect(Collectors.toList());
        return collectList;
    }
}
