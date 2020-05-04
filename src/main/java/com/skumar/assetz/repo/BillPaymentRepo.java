package com.skumar.assetz.repo;

import java.math.BigInteger;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skumar.assetz.entity.BillPayment;

@Repository
public interface BillPaymentRepo extends JpaRepository<BillPayment, BigInteger>{
}
