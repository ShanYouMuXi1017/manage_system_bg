package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.CourseBasicInformationMAPPER;
import com.example.object.CourseBasicInformation;
import com.example.object.CourseSyllabusInformation;
import com.example.service.CourseBasicInformationSERVICE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CourseBasicInformationServiceIMPL extends ServiceImpl<CourseBasicInformationMAPPER, CourseBasicInformation> implements CourseBasicInformationSERVICE {


    @Autowired
    private CourseBasicInformationMAPPER courseMapper;

    public List<Map<String, Object>> getCourseTree() {
        List<CourseSyllabusInformation> courses = courseMapper.getCourses();
        Map<String, List<String>> courseMap = new LinkedHashMap<>();

        for (CourseSyllabusInformation course : courses) {
            courseMap
                    .computeIfAbsent(course.getMajor(), k -> new ArrayList<>())
                    .add(course.getCourseName());
        }

        List<Map<String, Object>> treeData = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : courseMap.entrySet()) {
            Map<String, Object> majorNode = new HashMap<>();
            majorNode.put("label", entry.getKey());
            List<Map<String, String>> children = new ArrayList<>();
            for (String courseName : entry.getValue()) {
                Map<String, String> courseNode = new HashMap<>();
                courseNode.put("label", courseName);
                children.add(courseNode);
            }
            majorNode.put("children", children);
            treeData.add(majorNode);
        }

        return treeData;
    }

    @Override
    public List<CourseBasicInformation> couresNameList(String coursename) {
        return courseMapper.courseMapper(coursename);
    }
}
