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
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

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


    /*
     * 教学大纲设置相关接口
     * */

    /*
     * 课程基本信息相关接口
     * */

    @ApiOperation("按当前用户查询")
    @GetMapping("/currentUser/{currentUserName}")
    public DataResponses getByCurrentUser(@PathVariable String currentUserName) {
        QueryWrapper<CourseSyllabusInformation> QueryWrapper = new QueryWrapper<>();
        QueryWrapper.eq("upload_user", currentUserName);

        return new DataResponses(true, courseSyllabusInformationService.list(QueryWrapper));
    }

    @ApiOperation("根据课程负责人查询对应课程")
    @PostMapping  ("/currentUser")
    public DataResponses getSyllabusCourse(@RequestBody HashMap<String, String> info) {
        QueryWrapper<CourseSyllabusInformation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("upload_user", info.get("upload_user"));
        return new DataResponses(true, courseSyllabusInformationMAPPER.selectList(queryWrapper));
    }

    @ApiOperation("添加")
    @PostMapping
    public DataResponses Add(@RequestBody CourseSyllabusInformation pages) {
        return new DataResponses(courseSyllabusInformationService.save(pages));
    }

    @ApiOperation("按id查询")
    @GetMapping("/{id}")
    public DataResponses getById(@PathVariable int id) {
        return new DataResponses(true, courseSyllabusInformationService.getById(id));
    }

    @ApiOperation("按id修改")
    @PutMapping()
    public DataResponses UpdateById(@RequestBody CourseSyllabusInformation data) {
        return new DataResponses(courseSyllabusInformationService.updateById(data));
    }

    @ApiOperation("查询全部")
    @GetMapping
    public DataResponses getAll() {
        return new DataResponses(true, courseSyllabusInformationService.list());
    }


    @Autowired
    private UserMAPPER userMAPPER;
    @ApiOperation("获取该教师的id")
    @GetMapping("/courseTeacherId{courseTeacherName}")
    public DataResponses getCourseTeacherId(@PathVariable String courseTeacherName) {
        QueryWrapper<User> QueryWrapper = new QueryWrapper<>();
        QueryWrapper.eq("teacher_name", courseTeacherName);
        return new DataResponses(true, userMAPPER.selectList(QueryWrapper));

    }

    @Autowired
    private CourseBasicInformationServiceIMPL courseBasicInformationService;
    @Autowired
    private CourseBasicInformationMAPPER courseBasicInformationMAPPER;

    @ApiOperation("添加或修改")
    @PostMapping("/saveCourseBasic")
    public DataResponses write(@RequestBody CourseBasicInformation pages) {
        QueryWrapper<CourseBasicInformation> queryWrapper = new QueryWrapper<>();
        Integer id = pages.getId();
        int courseId = (int)id;
        boolean exists = courseBasicInformationMAPPER.exists(queryWrapper.eq("id",courseId));
        if(exists){
            return new DataResponses(courseBasicInformationService.updateById(pages));
        }
        return new DataResponses(courseBasicInformationService.save(pages));
    }

    /*课程目标相关接口*/

    @Autowired
    private CourseTargetMAPPER courseTarget;

    @ApiOperation("获取该课程所有课程目标")
    @GetMapping("/currentTarget/{courseId}")
    public DataResponses getCurrentTarget(@PathVariable int courseId) {
        QueryWrapper<CourseTarget> QueryWrapper = new QueryWrapper<>();
        QueryWrapper.eq("course_id", courseId);
        return new DataResponses(true, courseTarget.selectList(QueryWrapper));
    }

    @ApiOperation("添加该课程课程目标")
    @PostMapping("/saveCourseTarget")
    public DataResponses addCourseTarget(@RequestBody CourseTarget Data) {
        return new DataResponses(true, courseTarget.insert(Data),String.valueOf(Data.getId()));
    }

    @ApiOperation("删除课程目标")
    @DeleteMapping("/delCourseTarget")
    public DataResponses DeleteCourseTarget(@RequestBody CourseTarget Data) {
        return new DataResponses(true, courseTarget.deleteById(Data));
    }

    @ApiOperation("修改课程目标")
    @PutMapping("/modCourseTarget")
    public DataResponses modifyCourseTarget(@RequestBody CourseTarget Data) {
        return new DataResponses(true, courseTarget.updateById(Data));
    }




    /*课程考核评价方式相关接口*/

    @ApiOperation("获取该课程所有课程目标")
    @GetMapping("/courseTarget/{courseId}")
    public DataResponses getCourseTarget(@PathVariable int courseId) {
        QueryWrapper<CourseTarget> QueryWrapper = new QueryWrapper<>();
        QueryWrapper.eq("course_id", courseId);
        return new DataResponses(true, courseTarget.selectList(QueryWrapper));
    }

}
