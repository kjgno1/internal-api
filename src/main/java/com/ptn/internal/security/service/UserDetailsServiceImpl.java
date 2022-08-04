package com.ptn.internal.security.service;

import com.ptn.internal.model.TblUserInfo;
import com.ptn.internal.repository.UserInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserInfoRepository userInfoRepository;
    public static Map<String, TblUserInfo> userDetailCache = new ConcurrentHashMap<>();

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        TblUserInfo userDetail = userDetailCache.get(userName);
        if (userDetail != null) {
            return UserDetailsImpl.build(userDetail);
        } else {
            userDetail = userInfoRepository.findByUserName(userName)
                    .orElseThrow(() -> new UsernameNotFoundException("User Not Found with userName: " + userName));
            if (userDetail != null) {
                userDetailCache.put(userName, userDetail);
                return UserDetailsImpl.build(userDetail);
            } else {
                log.error("UsernameNotFoundException [" + userName + "]");
                throw new UsernameNotFoundException(userName);
            }
        }
    }

}

