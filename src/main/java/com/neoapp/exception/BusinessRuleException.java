package com.neoapp.exception;

public class BusinessRuleException extends RuntimeException {
    private final ErrorCode errorCode;
    public BusinessRuleException(ErrorCode errorCode) {
        super(errorCode.getMensagem());
        this.errorCode = errorCode;
    }
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
