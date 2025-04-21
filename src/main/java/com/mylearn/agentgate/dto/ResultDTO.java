package com.mylearn.agentgate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultDTO<T> {
    public static final int SUCCESS = 0;
    public static final int FAIL = -1;

    private int code;
    private String message;
    private T data;

    public static <T> ResultDTO<T> success(T data) {
        return new ResultDTO<T>(SUCCESS, "success", data);
    }

    public static <T> ResultDTO<T> fail(String message) {
        return new ResultDTO<T>(FAIL, message, null);
    }

    public static <T> ResultDTO<T> of(int code, String message, T data) {
        return new ResultDTO<T>(code, message, data);
    }
}
