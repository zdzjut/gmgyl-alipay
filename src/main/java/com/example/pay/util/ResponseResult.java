package com.example.pay.util;

public class ResponseResult {
    private int flag;
    private String message;
    private  String heartBeatPacket;
    private Object data;

    public ResponseResult() {
    }

    public ResponseResult(int flag, String message) {
        this.flag = flag;
        this.message = message;
    }

    public ResponseResult(int flag, String message, Object data) {
        this.flag = flag;
        this.message = message;
        this.data = data;
    }

    public ResponseResult(int flag, String message, String heartBeatPacket, Object data) {
        this.flag = flag;
        this.message = message;
        this.heartBeatPacket = heartBeatPacket;
        this.data = data;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getHeartBeatPacket() {
        return heartBeatPacket;
    }

    public void setHeartBeatPacket(String heartBeatPacket) {
        this.heartBeatPacket = heartBeatPacket;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
