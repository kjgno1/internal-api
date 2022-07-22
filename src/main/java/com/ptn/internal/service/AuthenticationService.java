package com.ptn.internal.service;

import com.ptn.internal.model.TblUserInfo;
import com.ptn.internal.model.dto.auth.SignUpRequest;
import com.ptn.internal.repository.UserInfoRepository;
import com.ptn.internal.security.jwt.JwtUtils;
import com.ptn.internal.security.service.UserDetailsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthenticationService {
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    public boolean validateUserName(String userName) {
        return userInfoRepository.existsByUserName(userName);
    }

    public boolean validateEmail(String email) {
        return userInfoRepository.existsByEmail(email);
    }

    public TblUserInfo createUser(SignUpRequest signUpRequest) {
        TblUserInfo tblUserInfo = null;
        try {
            tblUserInfo = TblUserInfo.builder()
                    .userName(signUpRequest.getUserName())
                    .password(new BCryptPasswordEncoder().encode(signUpRequest.getPassword()))
                    .email(signUpRequest.getEmail())
                    .build();
            tblUserInfo = userInfoRepository.save(tblUserInfo);
            log.info(String.format("Created user: [%s] - [%s]", signUpRequest.getUserName(), signUpRequest.getEmail()));
        } catch (Exception e) {
            log.error("Exception: " + e.getMessage());
        }

        return tblUserInfo;
    }

    public String generateJwtToken(String mobile) {
        UserDetails user = userDetailsService.loadUserByUsername(mobile);

        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, AuthorityUtils.createAuthorityList("ROLE_USER"));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return jwtUtils.generateJwtToken(authentication);
    }

}
