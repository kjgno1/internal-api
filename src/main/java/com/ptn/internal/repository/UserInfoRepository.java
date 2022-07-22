package com.ptn.internal.repository;

import com.ptn.internal.model.TblUserInfo;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserInfoRepository extends CrudRepository<TblUserInfo, Long> {

    Optional<TblUserInfo> findByUserName(String userName);

    Optional<TblUserInfo> findByEmail(String email);

    boolean existsByUserName(String userName);

    boolean existsByEmail(String email);
}
