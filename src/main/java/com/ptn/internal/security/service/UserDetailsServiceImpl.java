package com.ptn.internal.security.service;

import com.ptn.internal.model.TblUserInfo;
import com.ptn.internal.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserInfoRepository userInfoRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        TblUserInfo login = userInfoRepository.findByUserName(userName)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with userName: " + userName));

        return UserDetailsImpl.build(login);
    }

}

