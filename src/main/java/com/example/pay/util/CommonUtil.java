package com.example.pay.util;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CommonUtil {
    public static boolean isFeeEqual(String a,String b){
        int i = new BigDecimal(a).compareTo(new BigDecimal(b));
        return i==0;
    }

    /**

     * 生成32位md5码
     * @param content 待加密的内容
     */
    public static String toMD5(String content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("md5");
            byte[] result = digest.digest(content.getBytes());
            StringBuffer buffer = new StringBuffer();
            for (byte b : result) {
                int number = b & 0xff;// 加盐
                String str = Integer.toHexString(number);
                if (str.length() == 1) {
                    buffer.append("0");
                }
                buffer.append(str);
            }
            return buffer.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }

    }
}
