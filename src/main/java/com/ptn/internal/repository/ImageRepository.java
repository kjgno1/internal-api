package com.ptn.internal.repository;

import com.ptn.internal.model.TblImageInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;

public interface ImageRepository extends JpaRepository<TblImageInfo, BigInteger> {
    @Query(value = "select t.* from Tbl_Image_Info t where t.status= ?1 and t.type = ?3 order by t.modified_Date limit ?2 ", nativeQuery = true)
    List<TblImageInfo> getAllListImage(int status, int limit, String type);

    @Modifying
    @Transactional
    @Query(value = "update Tbl_Image_Info  set status = ?2 where id = ?1", nativeQuery = true)
    int updateStatusImage(BigInteger id, int status);
}
