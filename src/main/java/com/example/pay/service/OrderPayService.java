package com.example.pay.service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeCloseModel;
import com.alipay.api.domain.AlipayTradeFastpayRefundQueryModel;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.request.AlipayTradeFastpayRefundQueryRequest;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.example.pay.configuration.alipay.AlipayProperties;
import com.example.pay.model.PayInfoH2;
import com.example.pay.model.RefundInfo;
import com.example.pay.repository.PayInfoH2Repository;
import com.example.pay.repository.RefundInfoRepository;
import com.example.pay.service.activemq.service.SendMessageAfterPayService;
import com.example.pay.util.ResponseResult;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

@Service
public class OrderPayService {

    @Autowired
    private PayInfoH2Repository payInfoH2Repository;
    @Autowired
    private RefundInfoRepository refundInfoRepository;
    @Autowired
    private AlipayClient alipayClient;

    @Autowired
    private AlipayProperties aliPayProperties;
    @Autowired
    private SendMessageAfterPayService sendMessageAfterPayService;

    public void orderStartPay(HttpServletResponse response, PayInfoH2 payInfoH2) {
        try {
            String productCode = "FAST_INSTANT_TRADE_PAY";
            AlipayTradePagePayModel model = new AlipayTradePagePayModel();
            //储存至缓存
//            String id = UUID.randomUUID().toString().replaceAll("-", "");
//            payInfoH2.setId(id);
            payInfoH2.setProductCode(productCode);
            payInfoH2.setDeleteFlag("NO");
            payInfoH2Repository.save(payInfoH2);

            model.setOutTradeNo(payInfoH2.getId());
            model.setSubject(payInfoH2.getSubject());
            model.setBody(payInfoH2.getBody());
            model.setTotalAmount(payInfoH2.getTotalAmount());

            model.setProductCode(productCode);

            AlipayTradePagePayRequest pagePayRequest = new AlipayTradePagePayRequest();
            pagePayRequest.setReturnUrl(aliPayProperties.getReturnUrl());
            pagePayRequest.setNotifyUrl(aliPayProperties.getNotifyUrl());
            pagePayRequest.setBizModel(model);
            sendMessageAfterPayService.sendToKuajing(new ResponseResult(1,"提交订单",payInfoH2));
            String form = alipayClient.pageExecute(pagePayRequest).getBody();
            response.setContentType("text/html;charset=" + aliPayProperties.getCharset());
            response.getWriter().write(form);
            response.getWriter().flush();
            response.getWriter().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 交易关闭
     */
    public ResponseResult closeBusiness(String id) {
        try {
            AlipayTradeCloseRequest alipayRequest = new AlipayTradeCloseRequest();
            AlipayTradeCloseModel model = new AlipayTradeCloseModel();
            model.setOutTradeNo(id);
//            处理本地状态
            PayInfoH2 one = payInfoH2Repository.findPayInfoH2ById(id);
            one.setDeleteFlag("CLOSE");
            payInfoH2Repository.save(one);
            alipayRequest.setBizModel(model);
            AlipayTradeCloseResponse alipayResponse = alipayClient.execute(alipayRequest);
            return this.resultCase(alipayResponse.getBody(), "alipay_trade_close_response", "sub_msg");
        } catch (AlipayApiException e) {
            return new ResponseResult(0, "", e);
        }
    }

    public ResponseResult refund(RefundInfo info) {
        try {
            info.setDeleteFlag("NO");// 退款还没通过
            AlipayTradeRefundModel model = new AlipayTradeRefundModel();
            model.setOutTradeNo(info.getOutTradeNo());
            model.setRefundAmount(info.getRefundAmount());
            model.setRefundReason(info.getRefundReason());
            PayInfoH2 one = payInfoH2Repository.findPayInfoH2ById(info.getOutTradeNo());
            model.setTradeNo(one.getTradeNo());
            model.setOutRequestNo(info.getId());
            AlipayTradeRefundRequest alipayRequest = new AlipayTradeRefundRequest();
            alipayRequest.setBizModel(model);
            AlipayTradeRefundResponse alipayResponse = alipayClient.execute(alipayRequest);
            ResponseResult responseResult = this.resultCase(alipayResponse.getBody(), "alipay_trade_refund_response", "msg");
            if (responseResult.getFlag() == 1) {
                info.setDeleteFlag("YES");// 退款通过
            } else {
                info.setDeleteFlag("FAIL");// 退款失败
            }
            refundInfoRepository.save(info);
            return responseResult;
        } catch (AlipayApiException e) {
            return new ResponseResult(0, "", e);
        }
    }

    public ResponseResult refundQuery(String id, String outTradeNo) {
        try {
            AlipayTradeFastpayRefundQueryRequest alipayRequest = new AlipayTradeFastpayRefundQueryRequest();
            PayInfoH2 one = payInfoH2Repository.findPayInfoH2ById(outTradeNo);
            AlipayTradeFastpayRefundQueryModel model = new AlipayTradeFastpayRefundQueryModel();
            model.setOutTradeNo(outTradeNo);
            model.setTradeNo(one.getTradeNo());
            model.setOutRequestNo(id);
            alipayRequest.setBizModel(model);
            AlipayTradeFastpayRefundQueryResponse alipayResponse = alipayClient.execute(alipayRequest);
            return this.resultCase(alipayResponse.getBody(), "alipay_trade_fastpay_refund_query_response", "msg");
        } catch (AlipayApiException e) {
            return new ResponseResult(0, "", e);
        }
    }

    /**
     * 公共错误码
     * https://docs.open.alipay.com/common/105806 错误码文档
     *
     * @return
     */
    public ResponseResult resultCase(String body, String key, String msg) {
        System.out.println(body);
        JsonObject returnData = new JsonParser().parse(body).getAsJsonObject();
        JsonObject object = returnData.get(key).getAsJsonObject();
        String subMsg = object.get(msg).getAsString();
        String code = object.get("code").getAsString();
        if ("10000".equals(code)) {
            return new ResponseResult(1, "");
        } else if ("20000".equals(code)) {
            return new ResponseResult(0, "服务不可用", subMsg);
        } else if ("20001".equals(code)) {
            return new ResponseResult(0, "授权权限不足", subMsg);
        } else if ("40001".equals(code)) {
            return new ResponseResult(0, "缺少必选参数", subMsg);
        } else if ("40002".equals(code)) {
            return new ResponseResult(0, "非法的参数", subMsg);
        } else if ("40004".equals(code)) {
            return new ResponseResult(0, "业务处理失败", subMsg);
        } else if ("40006".equals(code)) {
            return new ResponseResult(0, "权限不足", subMsg);
        } else {
            return new ResponseResult(0, "不在支付宝返回示例中", body);
        }
    }


}
