package com.example.object.finalExamine;

import lombok.Data;

@Data
public class StudentComprehensiveScore {
    private Integer id;
    //学号
    private String studentNumber;
    //姓名
    private String studentName;
    //班级
    private String className;
    //课程信息外键
    private String courseId;

    //用于构建综合成绩的私有属性
    private double comprehensiveScore;
    //还没做
    private double experimentScore;

    private double usualScore;
    private double finalScore;

    //test
    private String ExamScore;
}

