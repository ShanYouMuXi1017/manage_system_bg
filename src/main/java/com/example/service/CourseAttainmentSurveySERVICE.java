package com.example.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.example.object.courseSurvey.CourseAttainmentSurvey;
import com.example.object.courseSurvey.CoursePO;
import com.example.object.courseSurvey.SurveyPO;
import com.example.object.courseSurvey.SurveyVO;

import java.util.List;


public interface CourseAttainmentSurveySERVICE extends IService<CourseAttainmentSurvey> {

    List<CoursePO> getCoursesByStudentNumber(String number);

    List<SurveyPO> getSurvey(String number, Integer courseId);

    List<SurveyVO> getTable(Integer courseId);
}
