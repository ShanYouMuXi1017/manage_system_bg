package com.example.object.courseSurvey;

import lombok.Data;

@Data
public class CourseAttainmentSurvey {

    private Integer studentId;
    //课程目标id
    private Integer courseTargetId;
    private Integer courseId;//课程id
    private String studentNumber;//学号
    private Integer attainment;//满意度等级

}
