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


    @ApiOperation("获取横向对比分析数据")
    @GetMapping("/getScoreCrosswiseAnalyse")
    public DataResponses getCourseScores(@RequestParam List<String> courseName, @RequestParam Integer teacherId) {
        // 调用服务层的方法获取详细的成绩数据
        Map<String, Object> response = statisticalAnalysisService.getScoresByCoursesAndTeacherId(courseName, teacherId);
        return new DataResponses(true, response);
    }
}
