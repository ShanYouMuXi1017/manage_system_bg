package com.example.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.mapper.CourseBasicInformationMAPPER;
import com.example.mapper.comprehensiveAnalyse.ExamPaperAnalyseReportMAPPER;
import com.example.object.comprehensiveAnalyse.ExamPaperAnalyseReport;
import com.example.service.impl.AnalysisReportServiceIMPL;
import com.example.utility.DataResponses;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@CrossOrigin(origins = "*")
@Api(tags = "分析报告")
@RestController
@RequestMapping("/report")
public class AnalysisReportController {
    @Autowired
    private AnalysisReportServiceIMPL analysisReportServiceIMPL;
    @Autowired
    private ExamPaperAnalyseReportMAPPER examPaperAnalyseReportMAPPER;
    @Autowired
    private CourseBasicInformationMAPPER courseBasicInformationMAPPER;


    @ApiOperation("老师更新当前课程分析报告生成状态并将其存储到本地")
    @GetMapping("/{courseId}/updateStatus")
    public DataResponses updateStatus(HttpServletResponse response, @PathVariable int courseId) {
        return analysisReportServiceIMPL.updateStatus(response, courseId, 1);
    }

    @ApiOperation("导出课程目标达成评价分析报告")
    @GetMapping("{courseId}/{type}/analyse")
    public ResponseEntity<byte[]> getAnalysisReport(HttpServletResponse response, @PathVariable int courseId, @PathVariable int type) {
        return analysisReportServiceIMPL.getReport(response, courseId, type);
    }

    @ApiOperation("导出课程试卷分析报告")
    @GetMapping("{courseId}/{type}/analyse3")
    public ResponseEntity<byte[]> getAnalysisReport3(HttpServletResponse response, @PathVariable int courseId, @PathVariable int type) {
        return analysisReportServiceIMPL.getReport3(response, courseId, type);
    }

    @ApiOperation("导出课程教学小结表")
    @GetMapping("{courseId}/{type}/analyse4")
    public ResponseEntity<byte[]> getAnalysisReport4(HttpServletResponse response, @PathVariable int courseId, @PathVariable int type) {
        return analysisReportServiceIMPL.getReport4(response, courseId, type);
    }

    @ApiOperation("获取改进措施")
    @GetMapping("{courseId}/getImprovementActions")
    public DataResponses getImprovementActions(@PathVariable int courseId) {
        QueryWrapper<ExamPaperAnalyseReport> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", courseId);
        return new DataResponses(true, examPaperAnalyseReportMAPPER.selectOne(queryWrapper));
    }

    @ApiOperation("添加及更新改进措施")
    @PostMapping("/InsertImprovementActions")
    public DataResponses InsertImprovementActions(@RequestBody ExamPaperAnalyseReport item) {
        QueryWrapper<ExamPaperAnalyseReport> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", item.getCourseId());
        if (examPaperAnalyseReportMAPPER.update(item, queryWrapper) == 0) {
            examPaperAnalyseReportMAPPER.insert(item);
        }
        return new DataResponses(true, item.getCourseId());
    }


}
