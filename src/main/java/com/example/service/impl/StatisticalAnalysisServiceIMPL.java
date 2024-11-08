package com.example.service.impl;

import com.example.mapper.StatisticalAnalysisMAPPER;
import com.example.object.CourseBasicInformation;
import com.example.service.StatisticalAnalysisSERVICE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            for (CourseBasicInformation courseInfo : courseInfos) {      //这一结构的意思是从 courseInfos 列表中获取每一个 CourseBasicInformation 对象
                // ，并将其赋值给 courseInfo 变量。循环会持续进行，直到遍历完列表中的所有元素
                Integer courseId = courseInfo.getId();
                String className = courseInfo.getClassName();
                // 获取该课程ID对应的班级成绩分析
                Map<String, Object> scores = statisticalAnalysisMapper.getScoresByCourseId(courseId);

                // 检查scores是否为null
                if (scores != null) {
                    // 将课程ID和成绩数据一起添加到结果集合中
                    Map<String, Object> courseData = new HashMap<>();
                    courseData.put("scores", scores);
                    courseData.put("className", className);

                    // 使用课程ID作为键
                    result.put("courseId_" + courseId, courseData);
                }
            }
        }

        return result;
    }
    @Override
    public Map<String, Object> getScoresByCoursesAndTeacherIdAndMajor(List<String> courseNames, Integer teacherId, String major) {
        // 创建一个结果集合
        Map<String, Object> result = new HashMap<>();

        // 遍历每个课程名称
        for (String courseName : courseNames) {
            // 根据课程名称、教师ID和专业获取课程信息列表
            List<CourseBasicInformation> courseInfos =
                    statisticalAnalysisMapper.getCourseIdsByCourseNameAndTeacherIdAndMajor(courseName, teacherId, major);

            // 遍历每个课程信息
            for (CourseBasicInformation courseInfo : courseInfos) {
                Integer courseId = courseInfo.getId();
                String className = courseInfo.getClassName();

                // 获取该课程ID对应的班级成绩分析
                Map<String, Object> scores = statisticalAnalysisMapper.getScoresByCourseId(courseId);

                // 检查scores是否为null
                if (scores != null) {
                    // 将课程ID和成绩数据一起添加到结果集合中
                    Map<String, Object> courseData = new HashMap<>();
                    courseData.put("scores", scores);
                    courseData.put("className", className);

                    // 使用课程ID作为键
                    result.put("courseId_" + courseId, courseData);
                }
            }
        }

        return result;
    }

}


