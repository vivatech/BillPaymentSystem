package com.vivatech.biller.repository;

import com.vivatech.biller.model.Biller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillerRepository extends JpaRepository<Biller, Integer> {
    Biller findByBillerName(String billerName);
}
