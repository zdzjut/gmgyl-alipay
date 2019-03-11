package com.example.pay.configuration.alipay;


import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableConfigurationProperties(AlipayProperties.class)
public class AlipayConfiguration {

	@Autowired
	private AlipayProperties properties;

	/**
	 * alipay-trade-sdk
	 */
	@Bean
	public AlipayTradeService alipayTradeService() {
		return new AlipayTradeServiceImpl.ClientBuilder()
				.setGatewayUrl(properties.getGatewayUrl())
				.setAppid(properties.getAppid())
				.setPrivateKey(properties.getAppPrivateKey())
				.setAlipayPublicKey(properties.getAlipayPublicKey())
				.setSignType(properties.getSignType())
				.build();
	}

	/**
	 * alipay-sdk-java
	 */
	@Bean
	public AlipayClient alipayClient(){
		return new DefaultAlipayClient(properties.getGatewayUrl(),
				properties.getAppid(),
				properties.getAppPrivateKey(),
				properties.getFormate(),
				properties.getCharset(),
				properties.getAlipayPublicKey(),
				properties.getSignType());
	}
}
