package com.example.object;

import lombok.Data;

@Data
public class CourseExamineMethods {
    private Integer id;
    //课程基本信息表中的id
    private int courseId;
    //课程名称
    private String courseName;
    //考试项目
    private String examineItem;
    //所占百分比
    private int percentage;
    //项目分数
    private int itemScore;
}
