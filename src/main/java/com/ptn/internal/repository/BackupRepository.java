package com.ptn.internal.repository;

import com.ptn.internal.model.TblBackup;
import com.ptn.internal.model.TblImageInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigInteger;
import java.util.List;

public interface BackupRepository extends JpaRepository<TblBackup, BigInteger> {
    @Query(value = "select t.* from Tbl_backup t  order by t.modified_Date limit ?1 ", nativeQuery = true)
    List<TblBackup> getAllListBackup(int limit);
}