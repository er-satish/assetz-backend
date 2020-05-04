package com.skumar.assetz.entity;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
@Entity
@Table(name = "billpayment")
public class BillPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;
    @NotBlank
    private String bank;
    @Column(name = "dueamt")
    private BigDecimal dueAmt;
    @Column(name = "duedate")
    private LocalDate dueDt;
    @Column(name = "paidamt")
    private BigDecimal paidAmt;
    @Column(name = "paiddate")
    private LocalDate paidDt;
    @Column(name = "billdate")
    private LocalDate billDt;
}
