package com.example.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.mapper.CourseBasicInformationMAPPER;
import com.example.mapper.CourseSyllabusInformationMAPPER;
import com.example.mapper.CourseTargetMAPPER;
import com.example.mapper.UserMAPPER;
import com.example.object.CourseBasicInformation;
import com.example.object.CourseSyllabusInformation;
import com.example.object.CourseTarget;
import com.example.object.User;
import com.example.object.finalExamine.StudentInformation;
import com.example.service.impl.CourseBasicInformationServiceIMPL;
import com.example.service.impl.CourseSyllabusInformationIMPL;
import com.example.service.impl.UserServiceIMPL;
import com.example.utility.DataResponses;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/courseSyllabus")
public class CourseSyllabusInformationController {


    @Autowired
    private CourseSyllabusInformationIMPL courseSyllabusInformationService;

    @ApiOperation("添加")
    @PostMapping("/addcourse/")
    public DataResponses addcourse(@RequestBody CourseSyllabusInformation pages) {
        return new DataResponses(courseSyllabusInformationService.save(pages));
    }

    @ApiOperation("删除")
    @DeleteMapping
    public DataResponses delcourse(@RequestBody CourseSyllabusInformation course){
        return new DataResponses(courseSyllabusInformationService.removeById(course));
    }

    @ApiOperation("修改课程信息")
    @PutMapping("/updateCourse")
    public DataResponses updateCourse(@RequestBody CourseSyllabusInformation course) {
        return new DataResponses(true, courseSyllabusInformationService.updateById(course));
    }


    @Autowired
    private CourseSyllabusInformationMAPPER courseSyllabusInformationMAPPER;

    @ApiOperation("查询课程")
    @PostMapping("/searchCourse")
    public DataResponses getPdfList(@RequestBody HashMap<String, String> info) {
        QueryWrapper<CourseSyllabusInformation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("major", info.get("major"));
        queryWrapper.eq("course_type", info.get("type"));
        queryWrapper.eq("version",info.get("version"));


        return new DataResponses(true, courseSyllabusInformationMAPPER.selectList(queryWrapper));
    }

    /*新增时的下拉框选择*/
    /*@ApiOperation("查询专业")
    @GetMapping("/allCourse")
    public DataResponses getAll() {
        QueryWrapper<CourseSyllabusInformation> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("DISTINCT major");
        return new DataResponses(true, courseSyllabusInformationMAPPER.selectList(queryWrapper));
    }
    }*/

    @ApiOperation("查询课程名称")
    @GetMapping("/searchCourseName")
    public DataResponses searchCourseName() {
        QueryWrapper<CourseSyllabusInformation> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("DISTINCT course_name");
        return new DataResponses(true, courseSyllabusInformationMAPPER.selectList(queryWrapper));
    }

    @ApiOperation("查询课程代码")
    @GetMapping("/searchCourseCode")
    public DataResponses searchCourseCode() {
        QueryWrapper<CourseSyllabusInformation> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("DISTINCT course_code");
        return new DataResponses(true, courseSyllabusInformationMAPPER.selectList(queryWrapper));
    }

    @ApiOperation("查询课程负责人")
    @GetMapping("/searchCourseHead")
    public DataResponses searchCourseHead(@RequestBody HashMap<String, String> info) {
        QueryWrapper<CourseSyllabusInformation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("upload_user", info.get("course_head"));
        return new DataResponses(true, courseSyllabusInformationMAPPER.selectList(queryWrapper));
    }

    /*新增时的下拉框选择*/
    /*@ApiOperation("查询专业")
    @GetMapping("/allCourse")
    public DataResponses getAll() {
        QueryWrapper<CourseSyllabusInformation> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("DISTINCT major");
        return new DataResponses(true, courseSyllabusInformationMAPPER.selectList(queryWrapper));
    }

    @ApiOperation("查询课程类别")
    @GetMapping("/searchCourseType")
    public DataResponses searchCourseType() {
        QueryWrapper<CourseSyllabusInformation> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("DISTINCT course_type");
        return new DataResponses(true, courseSyllabusInformationMAPPER.selectList(queryWrapper));
    }*/

    @ApiOperation("获得教学大纲课程的所有专业和版本")
    @GetMapping("/syllabusCourseMajorsAndVersions")
    public DataResponses syllabusCourseMajorsAndVersions() {
        return courseSyllabusInformationService.getAllMajorsAndVersions();
    }

    @ApiOperation("录入培养方案课程")
    @PostMapping("/inputEducationProgramCourse")
    public DataResponses inputEducationProgramCourse(@RequestParam("file") MultipartFile file) {
        return courseSyllabusInformationService.inputEducationProgramCourse(file);
    }

    @ApiOperation("获取培养方案课程Excel填写模版")
    @GetMapping("/getInputEducationProgramCourseTemplate")
    public ResponseEntity<byte[]> getInputEducationProgramCourseTemple(HttpServletResponse response) {
        return courseSyllabusInformationService.getInputEducationProgramCourseTemplate(response);
    }

}
