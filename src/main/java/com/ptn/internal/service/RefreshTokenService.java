package com.ptn.internal.service;

import com.ptn.internal.exception.TokenRefreshException;
import com.ptn.internal.model.TblRefreshToken;
import com.ptn.internal.repository.RefreshTokenRepository;
import com.ptn.internal.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Value("${app.jwtRefreshExpirationMs}")
    private Long TblRefreshTokenDurationMs;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    public Optional<TblRefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public TblRefreshToken generateRefreshToken(String userName) {
        TblRefreshToken TblRefreshToken = new TblRefreshToken();

        TblRefreshToken.setTblUserInfo(userInfoRepository.findByEmail(userName).orElse(null));
        TblRefreshToken.setExpiryDate(Instant.now().plusMillis(TblRefreshTokenDurationMs));
        TblRefreshToken.setToken(UUID.randomUUID().toString());

        TblRefreshToken = refreshTokenRepository.save(TblRefreshToken);

        return TblRefreshToken;
    }

    public TblRefreshToken verifyExpiration(TblRefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException("101", "Refresh token was expired. Please make a new sign-in request", token.getToken());
        }

        return token;
    }
}