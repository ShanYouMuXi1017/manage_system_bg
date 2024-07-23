package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.object.CourseBasicInformation;

import java.util.List;
import java.util.Map;

public interface CourseBasicInformationSERVICE extends IService<CourseBasicInformation> {
    //mapper中添加的方法在此处声明
    //也可以通过@Override重写方法
    public List<Map<String, Object>> getCourseTree();

    public List<CourseBasicInformation> couresNameList(String coursename);
}
