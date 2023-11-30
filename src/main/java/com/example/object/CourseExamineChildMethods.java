package com.example.object;

import lombok.Data;

@Data
public class CourseExamineChildMethods {
    private Integer id;
    //课程考核方式表中的id
    private int courseExamineMethodsId;
    //考核子项目
    private String examineChildItem;
    //所占百分比
    private int childPercentage;
    //总分
    private int childScore;
    //课程目标（json）
    private String courseTarget;
    //指标点（json）
    private String indicatorPointsDetail;
}
