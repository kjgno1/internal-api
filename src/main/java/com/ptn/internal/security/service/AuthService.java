package com.ptn.internal.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    public Authentication authenticate(String username, String password) throws UsernameNotFoundException, BadCredentialsException {
        UserDetails user = userDetailsService.loadUserByUsername(username);

        BCryptPasswordEncoder b = new BCryptPasswordEncoder();
        if(!b.matches(password,user.getPassword()))
            throw new UsernameNotFoundException("Wrong password!");

        return new UsernamePasswordAuthenticationToken(user, null,
                AuthorityUtils.createAuthorityList("ROLE_USER"));
    }
}
