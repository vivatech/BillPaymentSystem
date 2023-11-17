package com.vivatech.biller.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class BillerTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;
    private String transactionId;
    private String responseTransactionId;
    private String status;
    private String reason;
    @ManyToOne
    private Biller biller;
    private Double amount;
}
