package com.example.service;

import java.util.List;
import java.util.Map;

public interface StatisticalAnalysisSERVICE {
    // 根据课程名称和教师ID获取成绩
    Map<String, Object> getScoresByCoursesAndTeacherId(List<String> courseNames, Integer teacherId);
}
