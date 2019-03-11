package com.example.pay.repository;

import com.example.pay.model.RefundInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RefundInfoRepository extends JpaRepository<RefundInfo,String> {

    List<RefundInfo> findAllByOutTradeNo(String outTradeNo);

}