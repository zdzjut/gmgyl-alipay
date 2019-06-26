package com.example.pay.controller;

import com.example.pay.service.ImageCodeService;
import com.example.pay.util.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/image")
public class ImageCodeController {
    @Autowired
    private ImageCodeService imageCodeService;
    /**
     * 随机生成验证码
     */
    @PostMapping("/verifyCode")
    public ResponseResult verifyCode(int width, int height) {
        return imageCodeService.verifyCode(width, height);
    }

    /**
     * 校验验证码
     * @param codeMD5 后台传输过去的message
     * @param code 用户填写的验证码
     */
    @PostMapping("/checkCode")
    public ResponseResult checkCode(String codeMD5, String code) {
        return imageCodeService.checkCode(codeMD5, code);
    }
}
