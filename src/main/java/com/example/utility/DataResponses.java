package com.example.utility;


import lombok.Data;


//前后端交互的数据模型
@Data
public class DataResponses {
    private Boolean flag = false;
    private Object data;
    private String message;


    public DataResponses(){}

    public DataResponses(Boolean flag){
        this.flag = flag;
    }

    public DataResponses(Boolean flag, Object data){
        this.flag = flag;
        this.data = data;
    }

    public DataResponses(Boolean flag,String message){
        this.flag = flag;
        this.message = message;
    }

    public DataResponses(int num){
        this.flag = num != 0;
    }

    public DataResponses(Boolean flag, Object data, String message) {
        this.flag = flag;
        this.data = data;
        this.message = message;
    }
}
