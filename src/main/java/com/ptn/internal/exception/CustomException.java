package com.ptn.internal.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private String errorCode;

    public CustomException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
