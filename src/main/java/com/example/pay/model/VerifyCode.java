package com.example.pay.model;

public class VerifyCode {
    private String id;
    private String code;
    private String imageString;

    public VerifyCode() {
    }

    public VerifyCode(String id, String code, String imageString) {
        this.id = id;
        this.code = code;
        this.imageString = imageString;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getImageString() {
        return imageString;
    }

    public void setImageString(String imageString) {
        this.imageString = imageString;
    }
}
