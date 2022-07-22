package com.ptn.internal.repository;

import com.ptn.internal.model.TblRefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<TblRefreshToken, Long> {

    Optional<TblRefreshToken> findByToken(String token);
}