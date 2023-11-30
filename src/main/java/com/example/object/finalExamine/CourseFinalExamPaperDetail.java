package com.example.object.finalExamine;

import lombok.Data;

@Data
public class CourseFinalExamPaperDetail {
    private Integer id;
    //外键链接courseFinalExamPaper表id
    private int primaryId;
    //题号
    private int titleNumber;
    private float score;
    //对应指标点
    private String indicatorPoints;
    //对应课程目标
    private String courseTarget;

}

