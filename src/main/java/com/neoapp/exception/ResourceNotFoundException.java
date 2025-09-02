package com.neoapp.exception;

public class ResourceNotFoundException extends RuntimeException {
    private final ErrorCode errorCode;
    public ResourceNotFoundException(ErrorCode errorCode) {
        super(errorCode.getMensagem());
        this.errorCode = errorCode;
    }
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
