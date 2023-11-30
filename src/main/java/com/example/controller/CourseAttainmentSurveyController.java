package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.mapper.CourseTargetMAPPER;
import com.example.mapper.courseSurvey.CourseAttainmentSurveyMAPPER;
import com.example.object.courseSurvey.CoursePO;
import com.example.object.CourseTarget;
import com.example.object.courseSurvey.CourseAttainmentSurvey;
import com.example.service.impl.CourseAttainmentSurveyServiceIMPL;
import com.example.utility.DataResponses;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@Api(tags = "课程信息")
@RestController
@RequestMapping("/survey")
public class CourseAttainmentSurveyController {

    @Autowired
    private CourseAttainmentSurveyServiceIMPL courseAttainmentSurveyServiceIMPL;

    @Autowired
    private CourseAttainmentSurveyMAPPER courseAttainmentSurveyMAPPER;


    //课程目标
    @Autowired
    private CourseTargetMAPPER courseTarget;

    @ApiOperation("获取当前学生的所有课程")
    @GetMapping("/getCoursesByStudentNumber")
    public DataResponses getCoursesByStudentNumber(@RequestParam String number) {
        List<CoursePO> courses = courseAttainmentSurveyServiceIMPL.getCoursesByStudentNumber(number);
        return new DataResponses(true, courses);
    }

    @ApiOperation("获取当前指定课程的目标")
    @GetMapping("/getCourseTarget/{courseId}")
    public DataResponses getCourseTarget(@PathVariable int courseId) {
        QueryWrapper<CourseTarget> QueryWrapper = new QueryWrapper<>();
        QueryWrapper.eq("course_id", courseId);
        return new DataResponses(true, courseTarget.selectList(QueryWrapper));
    }

    @ApiOperation("获取指定课程的问卷")
    @GetMapping("/getCourseSurvey")
    public DataResponses getCourseSurvey(@RequestParam int courseId, String number) {
        return new DataResponses(true, courseAttainmentSurveyServiceIMPL.getSurvey(number, courseId));
    }

    @ApiOperation("添加问卷")
    @PostMapping("/addSurvey")
    public DataResponses addSurvey(@RequestBody List<CourseAttainmentSurvey> surveys) {
        return new DataResponses(courseAttainmentSurveyServiceIMPL.saveBatch(surveys), "添加成功");
    }

    @ApiOperation("获取指定课程问卷的完成情况")
    @GetMapping("/{courseId}/getStudentComplete")
    public DataResponses getStudentComplete(@PathVariable int courseId) {
        return new DataResponses(true, courseAttainmentSurveyMAPPER.getStudent(courseId));
    }

    @ApiOperation("获取课程调查问卷详细数据")
    @GetMapping("/{courseId}/getSurveyTable")
    public DataResponses getSurveyTable(@PathVariable int courseId) {
        return new DataResponses(true, courseAttainmentSurveyServiceIMPL.getTable(courseId));
    }
}
