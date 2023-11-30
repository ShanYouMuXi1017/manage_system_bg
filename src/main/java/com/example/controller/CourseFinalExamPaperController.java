package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.mapper.examinePaper.CourseFinalExamPaperMAPPER;
import com.example.object.finalExamine.CourseFinalExamPaper;
import com.example.object.finalExamine.CourseFinalExamPaperDetail;
import com.example.service.impl.examinePaper.CourseFinalExamPaperDetailServiceIMPL;
import com.example.service.impl.examinePaper.CourseFinalExamPaperServiceIMPL;
import com.example.utility.DataResponses;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@CrossOrigin(origins = "*")
@Api(tags = "期末试卷成绩细明")
@RestController
@RequestMapping("/courseExamPaper")
public class CourseFinalExamPaperController {

/*
    期末考察项目
*/

    @Autowired
    private CourseFinalExamPaperServiceIMPL courseFinalExamPaperService;

    @Autowired
    private CourseFinalExamPaperMAPPER courseFinalExamPaperMAPPER;


    @ApiOperation("查询所有期末考察项目")
    @GetMapping()
    public DataResponses getAll() {
        return new DataResponses(true, courseFinalExamPaperService.list());
    }

    @ApiOperation("提供考察评价方式表的id查询期末考察项目")
    @GetMapping("/{examChildMethodId}")
    public DataResponses getById(@PathVariable int examChildMethodId) {
        QueryWrapper<CourseFinalExamPaper> QueryWrapper = new QueryWrapper<>();
        QueryWrapper.eq("exam_child_method_id", examChildMethodId);
        return new DataResponses(true, courseFinalExamPaperService.list(QueryWrapper));
    }

    @ApiOperation("提供考察评价方式表的id添加期末考察项目")
    @PostMapping()
    public DataResponses addById(@RequestBody CourseFinalExamPaper information) {

        return new DataResponses(courseFinalExamPaperService.save(information), information.getId());
    }

    @ApiOperation("提供id修改期末考察项目")
    @PutMapping()
    public DataResponses modifyById(@RequestBody CourseFinalExamPaper information) {
        return new DataResponses(courseFinalExamPaperService.updateById(information));
    }

    @ApiOperation("提供id删除期末考察项目")
    @DeleteMapping()
    public DataResponses deleteById(@RequestBody String id) {
        return new DataResponses(courseFinalExamPaperService.removeById(id));
    }

    /*
        项目细明
    */
    @Autowired
    private CourseFinalExamPaperDetailServiceIMPL courseFinalExamPaperDetailService;

//    @ApiOperation("查询所有期末考察项目细明")
//    @GetMapping("/detail")
//    public DataResponses getAllDetail() {
//        return new DataResponses(true, courseFinalExamPaperDetailService.list());
//    }

    @ApiOperation("提供期末试卷表的id查询详细期末考察项目")
    @GetMapping("/detail/{primaryId}")
    public DataResponses getDetailById(@PathVariable int primaryId) {
        QueryWrapper<CourseFinalExamPaperDetail> QueryWrapper = new QueryWrapper<>();
        QueryWrapper.eq("primary_id", primaryId);
        QueryWrapper.orderByAsc("title_number");
        return new DataResponses(true, courseFinalExamPaperDetailService.list(QueryWrapper));
    }

    @ApiOperation("根据期末试卷表的id添加详细期末考察项目")
    @PostMapping("/detail")
    public DataResponses addDetailById(@RequestBody CourseFinalExamPaperDetail information) {
        return new DataResponses(courseFinalExamPaperDetailService.save(information));
    }

    @ApiOperation("提供期末试卷表的id更新详细期末考察项目")
    @PutMapping("/detail")
    public DataResponses modifyDetailById(@RequestBody CourseFinalExamPaperDetail information) {
        return new DataResponses(courseFinalExamPaperDetailService.updateById(information));
    }

    @ApiOperation("提供期末试卷表的id删除详细期末考察项目")
    @DeleteMapping("/detail")
    public DataResponses deleteDetailById(@RequestBody String id) {
        return new DataResponses(courseFinalExamPaperDetailService.removeById(id));
    }

    /*
        相关的表格展示
     */
    @ApiOperation("提供课程ID生成该课程考核方式和指标点，课程目标的对应关系")
    @GetMapping("/{courseId}/{type}/Table")
    public ResponseEntity<byte[]> exportExcel(HttpServletResponse response, @PathVariable int courseId, @PathVariable int type) throws IOException {
        return courseFinalExamPaperDetailService.ExportExamPaperRelationExcel(response, courseId, type);
    }

}
