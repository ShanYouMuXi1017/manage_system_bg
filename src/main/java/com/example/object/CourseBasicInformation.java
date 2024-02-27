package com.example.object;

import lombok.Data;

@Data
public class CourseBasicInformation {
    private Integer id;
    //课程名称
    private String courseName;
    //专业
    private String major;
    //课程代码
    private String courseCode;
    //任课教师id
    private int teacherId;
    //任课教师
    private String classroomTeacher;
    //理论时长
    private int theoreticalHours;
    //实验学识
    private int labHours;
    //班级名称
    private String className;
    //学期
    private int term;
    private String termStart;
    private String termEnd;
    //教材
    private String textBook;
    //学生人数
    private int studentsNum;
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
    //
    private String accomplish;

}
