package com.example.object.courseSurvey;

import lombok.Data;

@Data
public class SurveyPO {
    private Integer studentId;
    private Integer courseId;
    private Integer courseTargetId;
    private String targetName;
    private String courseTarget;
    private Integer attainment;
}
