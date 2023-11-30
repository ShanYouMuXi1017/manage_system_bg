package com.example.object.courseSurvey;

import lombok.Data;

@Data
public class CoursePO {
    private Integer courseId;//课程Id
    private String courseName; //课程名称
    private Boolean survey;//学生是否已填过此课程的调查问卷
}
