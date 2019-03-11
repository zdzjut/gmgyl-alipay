package com.example.pay.controller;

import com.example.pay.repository.PayInfoH2Repository;
import com.example.pay.repository.RefundInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/h2")
public class H2Controller {
    @Autowired
    private PayInfoH2Repository payInfoH2Repository;
    @Autowired
    private RefundInfoRepository refundInfoRepository;

    @PostMapping("/queryAll")
    public Object queryAll() {
        return payInfoH2Repository.findAll();
    }

    @PostMapping("/queryOne")
    public Object queryOne(String id) {
        return payInfoH2Repository.findPayInfoH2ById(id);
    }

    @PostMapping("/queryAllRefund")
    public Object queryAllRefund(String outTradeNo) {
        return refundInfoRepository.findAllByOutTradeNo(outTradeNo);
    }
}
