package com.example.pay.repository;

import com.example.pay.model.PayInfoH2;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayInfoH2Repository extends JpaRepository<PayInfoH2, String> {
    PayInfoH2 findPayInfoH2ById(String id);
}