package com.example.object;

import lombok.Data;

@Data
public class CourseSyllabusInformation {
    private Integer id;
    //课程名称
    private String courseName;
    //课程代码
    private String courseCode;
    //专业
    private String major;
    //学分
    private Double credit;
    //理论时长
    private int theoreticalHours;
    //实验学识
    private int labHours;
    //课程性质
    private String courseNature;
    //课程类别
    private String courseType;
    //暂不设置
//    //课程目标数量
//    private int courseTargetNum;
//    //指标点数量
//    private int indicatorPointsNum;
    //pdf文件地址
    private String fileAddress;
    private String uploadUser;

}
