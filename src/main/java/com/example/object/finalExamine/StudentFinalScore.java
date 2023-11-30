package com.example.object.finalExamine;

import lombok.Data;

@Data
public class StudentFinalScore extends StudentInformation{
    private Integer finalScoreId;
    //学生id
    private Integer studentId;
    //总成绩
    private double score;

    //题型和分值
    private String scoreDetails;

    //题型和分值返回值为二维数组
    private Object scoreResponse;
}

