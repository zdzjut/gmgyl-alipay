package com.example.pay.model;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "pay_info_h2")
public class PayInfoH2 {
    @Id
    private String id;
    private String tradeNo;
    private String subject;
    private String totalAmount;
    private String body;
    private String productCode;
    private String dateString;
    private String deleteFlag; //YES 已被删除 NO 未被删除

    public PayInfoH2() {
    }

    public PayInfoH2(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }


    public String getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(String deleteFlag) {
        this.deleteFlag = deleteFlag;
    }



    @Override
    public String toString() {
        return "PayInfoH2{" +
                "id='" + id + '\'' +
                ", tradeNo='" + tradeNo + '\'' +
                ", subject='" + subject + '\'' +
                ", totalAmount='" + totalAmount + '\'' +
                ", body='" + body + '\'' +
                ", productCode='" + productCode + '\'' +
                ", dateString='" + dateString + '\'' +
                ", deleteFlag='" + deleteFlag + '\'' +
                '}';
    }
}
