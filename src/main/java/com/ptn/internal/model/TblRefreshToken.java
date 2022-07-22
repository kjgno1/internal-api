package com.ptn.internal.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "TBL_REFRESH_TOKEN")
@Getter
@Setter
public class TblRefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_token_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "login_id", referencedColumnName = "id")
    private TblUserInfo tblUserInfo;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Instant expiryDate;

}