package com.example.pay.util;

import java.math.BigDecimal;

public class CommonUtil {
    public static boolean isFeeEqual(String a,String b){
        int i = new BigDecimal(a).compareTo(new BigDecimal(b));
        return i==0;
    }

}
