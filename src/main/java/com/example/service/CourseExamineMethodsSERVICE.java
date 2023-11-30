package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.object.CourseAllInformation;
import com.example.object.CourseExamineMethods;

import java.util.List;

public interface CourseExamineMethodsSERVICE extends IService<CourseExamineMethods> {
    //mapper中添加的方法在此处声明
    //也可以通过@Override重写方法
    List<CourseAllInformation> getAllInformation(int id);
}
