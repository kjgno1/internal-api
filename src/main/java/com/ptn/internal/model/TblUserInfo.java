package com.ptn.internal.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;
import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.Date;

@Entity
@Table(name = "TBL_USER_INFO")
@Getter
@Setter
@Builder
@AllArgsConstructor
public class TblUserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;


    @Column(name = "password")
    private String password;

    @Column(name = "username")
    private String userName;

    @Column(name = "modified_date")
    @UpdateTimestamp
    private Date modifiedDate;

    @Email
    private String email;

    public TblUserInfo() {

    }
}
