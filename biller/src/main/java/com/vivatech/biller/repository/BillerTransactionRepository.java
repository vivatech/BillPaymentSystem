package com.vivatech.biller.repository;

import com.vivatech.biller.model.BillerTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillerTransactionRepository extends JpaRepository<BillerTransaction, Integer> {
    BillerTransaction findByTransactionId(String orderId);
}
