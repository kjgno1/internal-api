package com.ptn.internal.exception;

import lombok.Getter;

//@ResponseStatus(HttpStatus.FORBIDDEN)
@Getter
public class TokenRefreshException extends CustomException {

    private String refreshToken;

    public TokenRefreshException(String errorCode, String errorMessage, String refreshToken) {
        super(errorCode, errorMessage);
        this.refreshToken = refreshToken;
    }
}
