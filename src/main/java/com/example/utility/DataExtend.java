package com.example.utility;


import lombok.Data;

//多数据模型
@Data
public class DataExtend {
    private String message;
    private String data;

    public DataExtend(String message, String data) {
        this.message = message;
        this.data = data;
    }

    public DataExtend(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
