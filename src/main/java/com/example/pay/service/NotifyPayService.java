package com.example.pay.service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.demo.trade.model.builder.AlipayTradeQueryRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FQueryResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.example.pay.configuration.alipay.AlipayProperties;
import com.example.pay.model.PayInfoH2;
import com.example.pay.repository.PayInfoH2Repository;
import com.example.pay.service.activemq.service.SendMessageAfterPayService;
import com.example.pay.util.CommonUtil;
import com.example.pay.util.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class NotifyPayService {

    @Autowired
    private PayInfoH2Repository payInfoH2Repository;
    @Autowired
    private AlipayProperties aliPayProperties;

    @Autowired
    private AlipayTradeService alipayTradeService;
    @Autowired
    private SendMessageAfterPayService sendMessageAfterPayService;

    public void notify(HttpServletRequest request) {
        try {
            System.out.println("异步通知签名校验");
            boolean flag = this.rsaCheckV1(request);
            if (!flag) {
                System.out.println("验签失败");
                return;
            }
            String outTradeNo = new String(request.getParameter("out_trade_no").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8); // 商户订单号
            String totalAmount = new String(request.getParameter("total_amount").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8); //付款金额
            String sellerId = new String(request.getParameter("seller_id").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            PayInfoH2 one = payInfoH2Repository.findPayInfoH2ById(outTradeNo);
            if (one.getDeleteFlag() == null || one.getDeleteFlag().equals("YES") || !CommonUtil.isFeeEqual(totalAmount, one.getTotalAmount()) || !aliPayProperties.getUid().equals(sellerId)) {
                System.out.println("outTradeNo查不到,或已经处理过，totalAmount不等，sellerId不等");//待删
                return;
            }
            //todo 并且过滤重复的通知结果数据。
            //交易状态 ,沙箱是TRADE_SUCCESS
            String tradeStatus = new String(request.getParameter("trade_status").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            //支付宝交易号
            String trade_no = new String(request.getParameter("trade_no").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            // TRADE_FINISHED(表示交易已经成功结束，并不能再对该交易做后续操作);
            // TRADE_SUCCESS(表示交易已经成功结束，可以对该交易做后续操作，如：分润、退款等);
            if (tradeStatus.equals("TRADE_FINISHED")) {
                //如果签约的是可退款协议，退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
                //如果没有签约可退款协议，那么付款完成后，支付宝系统发送该交易状态通知。
            } else if (tradeStatus.equals("TRADE_SUCCESS")) {
                //如果签约的是可退款协议，那么付款完成后，支付宝系统发送该交易状态通知。
                //删除缓存
                one.setDeleteFlag("YES");
                one.setTradeNo(trade_no);
                one.setDateString(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                payInfoH2Repository.save(one);
                sendMessageAfterPayService.sendToKuajing(new ResponseResult(1, "支付宝同步回调信息", one));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String returnUrl(HttpServletRequest request) {
        boolean verifyResult = this.rsaCheckV1(request);
        if (verifyResult) {
            String outTradeNo = new String(request.getParameter("out_trade_no").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8); //商户订单号
            String tradeNo = new String(request.getParameter("trade_no").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8); //支付宝交易号
            System.out.println("商户订单号:" + outTradeNo + "支付宝交易号:" + tradeNo);
            String totalAmount = new String(request.getParameter("total_amount").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8); //付款金额
            String sellerId = new String(request.getParameter("seller_id").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            PayInfoH2 one = payInfoH2Repository.findPayInfoH2ById(outTradeNo);
            if (one.getDeleteFlag() == null || one.getDeleteFlag().equals("YES") || !CommonUtil.isFeeEqual(totalAmount, one.getTotalAmount()) || !aliPayProperties.getUid().equals(sellerId)) {
                return "alreadyPay";
            }
            one.setDeleteFlag("YES");
            one.setTradeNo(tradeNo);
            payInfoH2Repository.save(one);
            sendMessageAfterPayService.sendToKuajing(new ResponseResult(1, "支付宝同步回调信息", one));
            return "paySuccess";
        } else {
            return "payFail";
        }
    }

    public ResponseResult queryOne(String orderNo) {
        PayInfoH2 payInfoH2ById = payInfoH2Repository.findPayInfoH2ById(orderNo);
        // 可能存在脏数据，notify可能随时修改状态
        if (payInfoH2ById != null && "YES".equals(payInfoH2ById.getDeleteFlag())) {
            return new ResponseResult(1, "该订单支付成功", payInfoH2ById);
        }
        AlipayTradeQueryRequestBuilder builder = new AlipayTradeQueryRequestBuilder().setOutTradeNo(orderNo);
        AlipayF2FQueryResult result = alipayTradeService.queryTradeResult(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                System.out.println(result.getResponse().toString());
                AlipayTradeQueryResponse response = result.getResponse();
                String tradeNo = response.getTradeNo();
                System.out.println(tradeNo);
                String totalAmount = response.getTotalAmount();
                System.out.println(totalAmount);
                Date sendPayDate = response.getSendPayDate();
                System.out.println(sendPayDate);
                String buyerUserId = response.getBuyerUserId();
                System.out.println(buyerUserId);
                String storeId = response.getStoreId();
                System.out.println("storeId" + storeId);
                PayInfoH2 one = payInfoH2Repository.findPayInfoH2ById(response.getOutTradeNo());
                if (one.getDeleteFlag() == null || one.getDeleteFlag().equals("YES") || !CommonUtil.isFeeEqual(totalAmount, one.getTotalAmount())) {
                    System.out.println("outTradeNo查不到,或已经处理过，totalAmount不等");//待删
                } else {
                    //成功了，但是未存储
                    payInfoH2Repository.save(one);
                    sendMessageAfterPayService.sendToKuajing(new ResponseResult(1, "支付宝同步回调信息", one));
                }
                return new ResponseResult(1, "查询返回该订单支付成功", one);
            case FAILED:
                return new ResponseResult(0, "查询返回该订单支付失败!!!");
            case UNKNOWN:
                return new ResponseResult(0, "交易返回异常");
            default:
                return new ResponseResult(0, "不支持的交易状态，交易返回异常!!!");
        }
    }

    /**
     * 校验签名
     */
    public boolean rsaCheckV1(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Map requestParams = request.getParameterMap();
        System.out.println("签名校验:");
        for (Object o : requestParams.keySet()) {
            String name = (String) o;
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        try {
            return AlipaySignature.rsaCheckV1(params, aliPayProperties.getAlipayPublicKey(), aliPayProperties.getCharset(), aliPayProperties.getSignType());
        } catch (AlipayApiException e) {
            return false;
        }

    }
}
