package com.example.service.impl;

import com.example.mapper.StatisticalAnalysisMAPPER;
import com.example.object.CourseBasicInformation;
import com.example.service.StatisticalAnalysisSERVICE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticalAnalysisServiceIMPL implements StatisticalAnalysisSERVICE {

    @Autowired
    private StatisticalAnalysisMAPPER statisticalAnalysisMapper;

    @Override
    public Map<String, Object> getScoresByCoursesAndTeacherId(List<String> courseNames, Integer teacherId) {
        // 创建一个结果集合
        Map<String, Object> result = new HashMap<>();

        // 遍历每个课程名称
        for (String courseName : courseNames) {
            // 根据课程名称和教师ID获取课程信息列表
            List<CourseBasicInformation> courseInfos = statisticalAnalysisMapper.getCourseIdsByCourseNameAndTeacherId(courseName, teacherId);

            // 遍历每个课程信息
            for (CourseBasicInformation courseInfo : courseInfos) {
                Integer courseId = courseInfo.getId();
                String className = courseInfo.getClassName();

                // 获取该课程ID对应的班级成绩分析，包括占比
                Map<String, Object> scores = statisticalAnalysisMapper.getScoresByCourseId(courseId);

                // 检查scores是否为null
                if (scores != null) {
                    // 将课程ID和成绩数据一起添加到结果集合中
                    Map<String, Object> courseData = new HashMap<>();
                    courseData.put("className", className);

                    // 使用安全的类型转换
                    double superiorRate = ((Number) scores.get("superior_rate")).doubleValue();
                    double greatRate = ((Number) scores.get("great_rate")).doubleValue();
                    double goodRate = ((Number) scores.get("good_rate")).doubleValue();
                    double passRates = ((Number) scores.get("pass_rates")).doubleValue();
                    double failedRate = ((Number) scores.get("failed_rate")).doubleValue();

                    // 直接使用数据库中的 pass_rate
                    String passRate = String.valueOf(((Number) scores.get("pass_rate")).doubleValue() * 100); // 直接获取

                    // 将占比数据添加到课程数据中
                    courseData.put("superior_rate", String.format("%.2f", superiorRate));
                    courseData.put("great_rate", String.format("%.2f", greatRate));
                    courseData.put("good_rate", String.format("%.2f", goodRate));
                    courseData.put("pass_rates", String.format("%.2f", passRates));
                    courseData.put("failed_rate", String.format("%.2f", failedRate));
                    courseData.put("pass_rate", passRate);

                    // 获取其他分数信息
                    courseData.put("average_score", ((Number) scores.get("average_score")).doubleValue());
                    courseData.put("max_score", ((Number) scores.get("max_score")).doubleValue());
                    courseData.put("min_score", ((Number) scores.get("min_score")).doubleValue());

                    // 使用课程ID作为键
                    result.put("courseId_" + courseId, courseData);
                }
            }
        }

        return result;
    }
}


