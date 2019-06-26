package com.example.pay.service;

import com.example.pay.model.VerifyCode;
import com.example.pay.util.CommonUtil;
import com.example.pay.util.ResponseResult;
import com.example.pay.util.VerifyCodeUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service
public class ImageCodeService {
    public ResponseResult verifyCode(int width, int height) {
        try {
            VerifyCode verifyCode = VerifyCodeUtils.verifyCode(width, height);
            String codeMD5 = CommonUtil.toMD5(verifyCode.getCode());
            return new ResponseResult(1, codeMD5, verifyCode.getImageString());
        } catch (IOException e) {
            return new ResponseResult(0, e.getMessage());
        }
    }

    public ResponseResult checkCode(String codeMD5, String code) {
        int flag = CommonUtil.toMD5(code).equals(codeMD5) ? 1 : 0;
        return new ResponseResult(flag, "校验结果，0错误，1正确");

    }
}
