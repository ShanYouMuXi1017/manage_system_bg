package com.example.object.comprehensiveAnalyse;

import lombok.Data;

@Data
public class CourseScoreAnalyse {
    private Integer id;
    private Integer courseId;
    private Integer studentNum;
    private double maxScore;
    private double minScore;
    private double averageScore;
    private Integer superior;
    private Integer great;
    private Integer good;
    private Integer pass;
    private Integer failed;
    private double passRate;
}
