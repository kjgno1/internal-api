package com.ptn.internal.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;

@Entity
@Table(name = "TBL_BACKUP")
@Getter
@Setter
public class TblBackup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id",unique=true, nullable = false)
    private BigInteger id;
    private String value;
    @UpdateTimestamp
    @Column(name = "modified_date")
    private Date modifiedDate;

    public TblBackup(String value) {
        this.value = value;
    }

    public TblBackup() {
    }
}
