package com.example.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.courseSurvey.CourseAttainmentSurveyMAPPER;
import com.example.object.courseSurvey.*;
import com.example.service.CourseAttainmentSurveySERVICE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseAttainmentSurveyServiceIMPL
        extends ServiceImpl<CourseAttainmentSurveyMAPPER, CourseAttainmentSurvey>
        implements CourseAttainmentSurveySERVICE {


    @Autowired
    CourseAttainmentSurveyMAPPER courseAttainmentSurveyMAPPER;


    /**
     * 通过学号获取学生所有课程
     * @param number 学号
     * @return 当前学生的所有课程
     */
    @Override
    public List<CoursePO> getCoursesByStudentNumber(String number) {
        List<CoursePO> courses = courseAttainmentSurveyMAPPER.getCourseByStudentNumber(number);
        List<Integer> survey = courseAttainmentSurveyMAPPER.isSurvey(number);
        for (CoursePO course : courses) {
            course.setSurvey(survey.contains(course.getCourseId()));
        }
        return courses;
    }

    /**
     * 通过学号和课程 id 获取当前登录学生指定课程的问卷
     * @param number 学号
     * @param courseId 课程 id
     * @return 问卷
     */
    @Override
    public List<SurveyPO> getSurvey(String number, Integer courseId) {
        return courseAttainmentSurveyMAPPER.getSurvey(number, courseId);
    }

    /**
     * 获取生成调查问卷结果所需数据
     * @param courseId 课程 id
     * @return 结果数据
     */
    @Override
    public List<SurveyVO> getTable(Integer courseId) {
        List<SurveyVO> totals = courseAttainmentSurveyMAPPER.getTotal(courseId);
        List<SurveyDAO> tables = courseAttainmentSurveyMAPPER.getTable(courseId);
        for (SurveyVO total : totals) {
            for (SurveyDAO table : tables) {
                if (table.getCourseTargetId().equals(total.getCourseTargetId())) {
                    switch (table.getAttainment()) {
                        case 0:
                            total.setGradeA(table.getAmount());
                            break;
                        case 1:
                            total.setGradeB(table.getAmount());
                            break;
                        case 2:
                            total.setGradeC(table.getAmount());
                            break;
                        case 3:
                            total.setGradeD(table.getAmount());
                            break;
                    }
                }
            }
        }
        return totals;

    }
}
