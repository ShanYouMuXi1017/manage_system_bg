package com.example.object.comprehensiveAnalyse;

import lombok.Data;

@Data
public class CourseTargetAnalyse {
    private Integer id;
    private Integer courseId;
    private Integer targetId;
    private String targetName;
    private Float value;
    private String matrix;
}
