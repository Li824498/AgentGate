package com.mylearn.agentgate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {
    public static final int SUCCESS = 0;
    public static final int FAIL = -1;

    private int code;
    private String message;
    private T data;

    public static <T> Result<T> success(T data) {
        return new Result<T>(SUCCESS, "success", data);
    }

    public static <T> Result<T> fail(String message) {
        return new Result<T>(FAIL, message, null);
    }

    public static <T> Result<T> of(int code, String message, T data) {
        return new Result<T>(code, message, data);
    }
}
