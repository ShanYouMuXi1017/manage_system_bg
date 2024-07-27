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

    public int getNewCourseId();

    /**
     * 使用继承方式创建课程时保存课程目标
     * @param afterSaveCourseId
     * @param toJiChengCourseId
     * @return
     */
    boolean saveInheritCourseTarget(Integer afterSaveCourseId,Integer toJiChengCourseId);

    /**
     * 使用继承方式创建课程时保存课程考核方式
     * @param afterSaveCourseId
     * @param toJiChengCourseId
     * @return
     */
    boolean saveInheritCourseMethod(Integer afterSaveCourseId,Integer toJiChengCourseId);

    /**
     * 使用继承方式创建课程时保存课程的试卷设置
     * @param afterSaveCourseId
     * @param toJiChengCourseId
     * @return
     */
    boolean saveInheritCoursePaper(Integer afterSaveCourseId,Integer toJiChengCourseId);
}
