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
    //教材
    private String textBook;
    //课程性质
    private String courseNature;
    //课程类别
    private String courseType;
    //课程目标数量
    private int courseTargetNum;
    //指标点数量
    private int indicatorPointsNum;
    //指标点编号
    private String indicatorPoints;
    //pdf文件地址
    private String fileAddress;
    private String uploadUser;
    //课程版本(依照培养方案)
    private String version;

    //解决Cannot determine value type from string“xxxxx”
    public CourseSyllabusInformation() {
    }

    //构造函数, 用于录入培养方案课程数据
    public CourseSyllabusInformation(String courseName, String courseCode, String major, Double credit, int theoreticalHours, int labHours, String courseNature, String courseType, String version) {
        this.courseName = courseName;
        if (courseCode.equals("null")) {
            this.courseCode = null;
        } else {
            this.courseCode = courseCode;
        }
        this.major = major;
        this.credit = credit;
        this.theoreticalHours = theoreticalHours;
        this.labHours = labHours;
        this.courseNature = courseNature;
        this.courseType = courseType;
        this.version = version;
    }
}
