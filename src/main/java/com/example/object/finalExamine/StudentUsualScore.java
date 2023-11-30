package com.example.object.finalExamine;

import lombok.Data;

@Data
public class StudentUsualScore extends StudentInformation{
    private Integer usualScoreId;
    //学生id
    private Integer studentId;
    //成绩
    private double score;

    private String scoreDetails;

    private Object scoreResponse;

}

