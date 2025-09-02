package com.neoapp.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

public class ApiResponse<T> {
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final boolean success;
    private final String code;
    private final String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T data;

    private  ApiResponse(boolean success, String code, String message, T data){
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, null,"Operação realizada com sucesso!", data);
    }
    public static ApiResponse<Object> success() {
        return new ApiResponse<>(true, null, "Operação realizada com sucesso!", null);
    }
    public static <T> ApiResponse<T> error(String code, String message, T data) {
        return new ApiResponse<>(false, code, message, data);
    }
    public static ApiResponse<Object> error(String code, String message) {
        return new ApiResponse<>(false, code, message, null);
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
    public String getCode() {
        return code;
    }
}
