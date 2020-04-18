package com.skumar.assetz.repo;

import java.math.BigInteger;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.skumar.assetz.entity.MfNavHistory;

@Repository
public interface MfNavHistoryRepo extends CrudRepository<MfNavHistory, BigInteger>{

}
