package com.ptn.internal.webservice;

import com.ptn.internal.model.dto.BaseResponse;
import com.ptn.internal.model.dto.auth.LoginRequest;
import com.ptn.internal.model.dto.auth.LoginResponse;
import com.ptn.internal.model.dto.auth.SignUpRequest;
import com.ptn.internal.model.dto.Status;
import com.ptn.internal.security.service.AuthService;
import com.ptn.internal.service.AuthenticationService;
import com.ptn.internal.service.RefreshTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/api/auth")
@Slf4j
public class AuthenticationWS extends BaseWS {
    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private AuthService authService;

    @Autowired
    private RefreshTokenService refreshTokenService;


    @PostMapping("/signUp")
    public ResponseEntity<BaseResponse> registerEmail(@Valid @RequestBody SignUpRequest request) {
        log.info(String.format("Register user: [%s] - [%s]", request.getUserName(), request.getEmail()));

        BaseResponse response = new BaseResponse();
        Status status = new Status();
        if (authenticationService.validateUserName(request.getUserName())) {
            status.setCode("004");
            status.setMessage("User name already in used");
        } else if (authenticationService.validateEmail(request.getEmail())) {
            status.setCode("005");
            status.setMessage("Email already in used");
        } else {
            authenticationService.createUser(request);

            status.setCode("000");
            status.setMessage("Email valid");
        }
        response.setStatus(status);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Login: " + loginRequest.getUserName());

        LoginResponse response = new LoginResponse();

        authService.authenticate(loginRequest.getUserName(),loginRequest.getPassword());

        response.setAccessToken(authenticationService.generateJwtToken(loginRequest.getUserName()));
        response.setRefreshToken(refreshTokenService.generateRefreshToken(loginRequest.getUserName()).getToken());
        Status status = new Status();
        status.setMessage("Login Successfully");
        status.setCode("0");
        response.setStatus(status);

        return ResponseEntity.ok(response);
    }
}
