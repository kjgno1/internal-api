package com.ptn.internal.model;

import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;

@Entity
@Table(name = "TBL_IMAGE_INFO")
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class TblImageInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id",unique=true, nullable = false)
    private BigInteger id;
    private String name;
    private String url;
    private String descriptions;
    private String tags;
    private String type;
    @UpdateTimestamp
    @Column(name = "modified_date")
    private Date modifiedDate;
    private int status = 0;



}
