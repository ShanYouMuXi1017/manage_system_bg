package com.example.object;

import lombok.Data;

@Data
public class CourseTarget {
    private Integer id;
    //课程基本信息表中的id
    private int courseId;
    //课程名
    private String courseName;
    //课程目标名
    private String targetName;
    //权重
    private double weight;
    //课程目标
    private String courseTarget;
    //达成途径
    private String pathWays;
    //对应指标点
    private String indicatorPoints;
    //评价依据
    private String evaluationMethod;
}
