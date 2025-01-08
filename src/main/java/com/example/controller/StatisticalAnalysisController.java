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

@CrossOrigin(origins = "*")
@Api(tags = "统计分析")
@RestController
@RequestMapping("/courseAnalysis")

/*该类下的方法被移动到AnalysisReportController 控制器下了，
原因是该控制器下的方法报404错误，如果有时间可以研究一下为什么*/
public class StatisticalAnalysisController {

    @Autowired
    private StatisticalAnalysisSERVICE statisticalAnalysisService;


    @ApiOperation("获取横向对比分析数据")
    @PostMapping("/getScoreCrosswiseAnalyse")
    public DataResponses getCourseScores(@RequestBody Map<String, Object> params) {
        List<String> courseName = (List<String>) params.get("courseName");
        Integer teacherId = (Integer) params.get("teacherId");
        // 调用服务层的方法获取详细的成绩数据
        Map<String, Object> response = statisticalAnalysisService.getScoresByCoursesAndTeacherId(courseName, teacherId);
        return new DataResponses(true, response);
    }
}
