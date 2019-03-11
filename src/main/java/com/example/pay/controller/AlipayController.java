package com.example.pay.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayDataDataserviceBillDownloadurlQueryModel;
import com.alipay.api.request.AlipayDataDataserviceBillDownloadurlQueryRequest;
import com.alipay.api.response.AlipayDataDataserviceBillDownloadurlQueryResponse;
import com.example.pay.configuration.alipay.AlipayProperties;
import com.example.pay.model.PayInfoH2;
import com.example.pay.model.RefundInfo;
import com.example.pay.service.NotifyPayService;
import com.example.pay.service.OrderPayService;
import com.example.pay.util.ResponseResult;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Controller
@RequestMapping("/alipay")
public class AlipayController {

    @Autowired
    private AlipayProperties aliPayProperties;

    @Autowired
    private AlipayClient alipayClient;

    @Autowired
    private OrderPayService orderPayService;
    @Autowired
    private NotifyPayService notifyPayService;

    @RequestMapping
    public String index() {
        return "gotoPagePay";
    }

    @ResponseBody
    @PostMapping("/gotoPayPage")
    public void gotoPayPage(HttpServletResponse response, PayInfoH2 payInfoH2) {
        orderPayService.orderStartPay(response, payInfoH2);
    }

    /**
     * 支付异步通知
     */
    @RequestMapping("/notify")
    public String notify(HttpServletRequest request) {
        notifyPayService.notify(request);
        return "paySuccess";
    }

    @RequestMapping("/returnUrl")
    public String returnUrl(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=" + aliPayProperties.getCharset());
        return notifyPayService.returnUrl(request);
    }

    /**
     * 订单查询(最主要用于查询订单的支付状态)
     */
    @ResponseBody
    @PostMapping("/query")
    public ResponseResult query(String id) {
        return notifyPayService.queryOne(id);
    }

    /**
     * 退款
     */
    @ResponseBody
    @PostMapping("/refund")
    public ResponseResult refund(RefundInfo info) {
        return orderPayService.refund(info);
    }

    /**
     * 退款查询
     *
     * @param id 请求退款接口时，传入的退款请求号，如果在退款请求时未传入，则该值为创建交易时的外部订单号
     */
    @ResponseBody
    @PostMapping("/refundQuery")
    public ResponseResult refundQuery(String id, String outTradeNo) {
        return orderPayService.refundQuery(id, outTradeNo);

    }

    /**
     * 关闭交易
     */
    @ResponseBody
    @PostMapping("/close")
    public ResponseResult close(String id) {
        return orderPayService.closeBusiness(id);
    }


    /**
     * billDate : 账单时间：日账单格式为yyyy-MM-dd，月账单格式为yyyy-MM。
     * 查询对账单下载地址: https://docs.open.alipay.com/api_15/alipay.data.dataservice.bill.downloadurl.query/
     */
    @ResponseBody
    @GetMapping("/bill")
    public void queryBill(String billDate) {
        System.out.println(billDate);
        // 1. 查询对账单下载地址
        AlipayDataDataserviceBillDownloadurlQueryRequest request = new AlipayDataDataserviceBillDownloadurlQueryRequest();
        AlipayDataDataserviceBillDownloadurlQueryModel model = new AlipayDataDataserviceBillDownloadurlQueryModel();
        model.setBillType("trade");
        model.setBillDate(billDate);
        request.setBizModel(model);
        try {
            AlipayDataDataserviceBillDownloadurlQueryResponse response = alipayClient.execute(request);
            if (response.isSuccess()) {
                String billDownloadUrl = response.getBillDownloadUrl();
                System.out.println(billDownloadUrl);

                // 2. 下载对账单
                List<String> orderList = this.downloadBill(billDownloadUrl);
                System.out.println(orderList);
                // 3. 先比较支付宝的交易合计/退款合计笔数/实收金额是否和自己数据库中的数据一致，如果不一致证明有异常，再具体找出那些订单有异常
                // 查找支付宝支付成功而自己支付失败的记录和支付宝支付失败而自己认为支付成功的异常订单记录到数据库

            } else {
                // 失败
                String code = response.getCode();
                String msg = response.getMsg();
                String subCode = response.getSubCode();
                String subMsg = response.getSubMsg();
                System.out.println(code + msg + subCode + subMsg);
            }
        } catch (AlipayApiException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 下载下来的是一个【账号_日期.csv.zip】文件（zip压缩文件名，里面有多个.csv文件）
     * 账号_日期_业务明细 ： 支付宝业务明细查询
     * 账号_日期_业务明细(汇总)：支付宝业务汇总查询
     * 注意：如果数据量比较大，该方法可能需要更长的执行时间
     */
    private List<String> downloadBill(String billDownLoadUrl) throws IOException {
        String ordersStr = "";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(60000)
                .setConnectionRequestTimeout(60000)
                .setSocketTimeout(60000)
                .build();
        HttpGet httpRequest = new HttpGet(billDownLoadUrl);
        httpRequest.setConfig(config);
        CloseableHttpResponse response = null;
        byte[] data;
        try {
            response = httpClient.execute(httpRequest);
            HttpEntity entity = response.getEntity();
            data = EntityUtils.toByteArray(entity);
        } finally {
            response.close();
            httpClient.close();
        }
        ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(data), Charset.forName("GBK"));
        ZipEntry zipEntry;
        try {
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                try {
                    String name = zipEntry.getName();
                    // 只要明细不要汇总
                    if (name.contains("汇总")) {
                        continue;
                    }
                    byte[] byteBuff = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = zipInputStream.read(byteBuff)) != -1) {
                        byteArrayOutputStream.write(byteBuff, 0, bytesRead);
                    }
                    ordersStr = byteArrayOutputStream.toString("GBK");
                } finally {
                    byteArrayOutputStream.close();
                    zipInputStream.closeEntry();
                }
            }
        } finally {
            zipInputStream.close();
        }
        if (ordersStr.equals("")) {
            return null;
        }
        String[] bills = ordersStr.split("\r\n");
        List<String> billList = Arrays.asList(bills);
        billList = billList.parallelStream().map(item -> item.replace("\t", "")).collect(Collectors.toList());
        return billList;
    }
}
