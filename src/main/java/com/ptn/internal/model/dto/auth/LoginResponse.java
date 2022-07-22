package com.ptn.internal.model.dto.auth;

import com.ptn.internal.model.dto.BaseResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse extends BaseResponse {
    private String accessToken;
    private String refreshToken;
}
