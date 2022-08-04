package com.ptn.internal.repository;

import com.ptn.internal.model.TblBackup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface BackupRepository extends JpaRepository<TblBackup, BigInteger> {

}