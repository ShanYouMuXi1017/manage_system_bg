package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.CourseExamineMethodsMAPPER;

import com.example.object.CourseAllInformation;
import com.example.object.CourseExamineMethods;
import com.example.service.CourseExamineMethodsSERVICE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseExamineMethodsServiceIMPL extends ServiceImpl<CourseExamineMethodsMAPPER, CourseExamineMethods> implements CourseExamineMethodsSERVICE {
    @Autowired
    private CourseExamineMethodsMAPPER courseExamineMethodsmapper;

    @Override
    public List<CourseAllInformation> getAllInformation(int id) {
        System.out.println(courseExamineMethodsmapper.getAllInformation(id));
        return courseExamineMethodsmapper.getAllInformation(id);
    }
}
