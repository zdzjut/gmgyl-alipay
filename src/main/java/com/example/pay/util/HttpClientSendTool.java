package com.example.pay.util;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


public class HttpClientSendTool {

    /**
     * 使用HttpURLConnection发送post
     */
    public static String sendPost(String urlParam, Map<String, Object> params, String charset) {
        StringBuffer resultBuffer;
        // 构建请求参数  
        StringBuilder sbParams = new StringBuilder();
        if (params != null && params.size() > 0) {
            for (Entry<String, Object> e : params.entrySet()) {
                sbParams.append(e.getKey());
                sbParams.append("=");
                sbParams.append(e.getValue());
                sbParams.append("&");
            }
        }
        HttpURLConnection con = null;
        OutputStreamWriter osw = null;
        BufferedReader br = null;
        // 发送请求  
        try {
            URL url = new URL(urlParam);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setUseCaches(false);
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            if (sbParams.length() > 0) {
                osw = new OutputStreamWriter(con.getOutputStream(), charset);
                osw.write(sbParams.substring(0, sbParams.length() - 1));
                osw.flush();
            }
            // 读取返回内容  
            resultBuffer = new StringBuffer();
            String headerField = con.getHeaderField("Content-Length");
            int contentLength = 0;
            if (headerField != null) {
                contentLength = Integer.parseInt(headerField);
            }

            if (contentLength > 0) {
                br = new BufferedReader(new InputStreamReader(con.getInputStream(), charset));
                String temp;
                while ((temp = br.readLine()) != null) {
                    resultBuffer.append(temp);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (osw != null) {
                try {
                    osw.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    con.disconnect();
                    con = null;
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                }
            }
        }

        return resultBuffer.toString();
    }
    public static void main(String[] args) {
        String url = "http://192.168.2.109:61008/appController/startFlowCommodity.do?commodityId=51432";
        Map<String, Object> map = new HashMap<>();
        String s = sendPost(url, map, "UTF-8");
        System.out.println(s);

    }

//    /**
//     * 使用URLConnection发送post
//     */
//    public static String sendPost2(String urlParam, Map<String, Object> params, String charset) {
//        StringBuffer resultBuffer;
//        // 构建请求参数
//        StringBuilder sb = new StringBuilder();
//        if (params != null && params.size() > 0) {
//            for (Entry<String, Object> e : params.entrySet()) {
//                sb.append(e.getKey());
//                sb.append("=");
//                sb.append(e.getValue());
//                sb.append("&");
//            }
//        }
//        URLConnection con;
//        OutputStreamWriter osw = null;
//        BufferedReader br = null;
//        try {
//            URL realUrl = new URL(urlParam);
//            // 打开和URL之间的连接
//            con = realUrl.openConnection();
//            // 设置通用的请求属性
//            con.setRequestProperty("accept", "*/*");
//            con.setRequestProperty("connection", "Keep-Alive");
//            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//            con.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
//            // 发送POST请求必须设置如下两行
//            con.setDoOutput(true);
//            con.setDoInput(true);
//            // 获取URLConnection对象对应的输出流
//            osw = new OutputStreamWriter(con.getOutputStream(), charset);
//            if (sb.length() > 0) {
//                // 发送请求参数
//                osw.write(sb.substring(0, sb.length() - 1));
//                // flush输出流的缓冲
//                osw.flush();
//            }
//            // 定义BufferedReader输入流来读取URL的响应
//            resultBuffer = new StringBuffer();
//            int contentLength = Integer.parseInt(con.getHeaderField("Content-Length"));
//            if (contentLength > 0) {
//                br = new BufferedReader(new InputStreamReader(con.getInputStream(), charset));
//                String temp;
//                while ((temp = br.readLine()) != null) {
//                    resultBuffer.append(temp);
//                }
//            }
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        } finally {
//            if (osw != null) {
//                try {
//                    osw.close();
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//            if (br != null) {
//                try {
//                    br.close();
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }
//        return resultBuffer.toString();
//    }



}