package com.example.controller;

import com.example.service.StatisticalAnalysisSERVICE;
import com.example.utility.DataResponses;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Api(tags = "统计分析")
@RestController
@RequestMapping("/courseAnalysis")
public class StatisticalAnalysisController {

    @Autowired
    private StatisticalAnalysisSERVICE statisticalAnalysisService;

//    @ApiOperation("获取横向对比分析数据")
//    @GetMapping("/getScoreCrosswiseAnalyse")
//    public DataResponses getCourseScores(@RequestParam List<String> courseName, @RequestParam Integer teacherId) {
//        Map<String, Object> response = statisticalAnalysisService.getScoresByCoursesAndTeacherId(courseName, teacherId);
//        return new DataResponses(true, response);
//    }
    @ApiOperation("获取横向对比分析数据")
    @GetMapping("/getScoreCrosswiseAnalyse")
    public DataResponses getCourseScores() {
        List<String> courseName = Arrays.asList("传感器技术与应用");
        Integer teacherId = 126;

        Map<String, Object> response = statisticalAnalysisService.getScoresByCoursesAndTeacherId(courseName, teacherId);
        return new DataResponses(true, response);
    }


//    @ApiOperation("获取纵向对比分析数据")
//    @GetMapping("/getScoresByCoursesAndTeacherIdAndMajor")
//    public DataResponses getScoresByCoursesAndTeacherIdAndMajor(
//            @RequestParam List<String> courseNames,
//            @RequestParam Integer teacherId,
//            @RequestParam String major) {
//        Map<String, Object> response = statisticalAnalysisService.getScoresByCoursesAndTeacherIdAndMajor(courseNames, teacherId, major);
//        return new DataResponses(true, response);
//    }
//}
    @ApiOperation("获取纵向对比分析数据")
    @GetMapping("/getScoreVerticalAnalyse")
    public DataResponses getScoresByCoursesAndTeacherIdAndMajor() {
        List<String> courseNames = Arrays.asList("传感器技术与应用"); // 固定课程名称
        Integer teacherId = 126; // 固定教师ID
        String major = "电子信息工程"; // 固定专业

        Map<String, Object> response = statisticalAnalysisService.getScoresByCoursesAndTeacherIdAndMajor(courseNames, teacherId, major);
        return new DataResponses(true, response);
    }
}


//    @ApiOperation("获取纵向分析数据")
//    @GetMapping("/getScoreVerticalAnalyse")
//    public Map<String, Object> getCourseStatistics(@RequestParam String courseName, @RequestParam String teacherId) {
//        return statisticalAnalysisService.getCourseStatistics(courseName, teacherId);
//    }
