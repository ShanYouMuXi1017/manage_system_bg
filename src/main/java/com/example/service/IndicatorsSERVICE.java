package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.object.Indicators;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletResponse;

public interface IndicatorsSERVICE extends IService<Indicators> {
    //mapper中添加的方法在此处声明
    //也可以通过@Override重写方法
    ResponseEntity<byte[]> IndicatorsPDF(String major, String version);

}
