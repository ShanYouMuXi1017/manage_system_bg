package com.example.mapper;

import com.example.object.CourseBasicInformation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface StatisticalAnalysisMAPPER {

//    横向对比
    @Select("SELECT id, class_name FROM course_basic_information WHERE course_name = #{courseName} AND teacher_id = #{teacherId}")
    List<CourseBasicInformation> getCourseIdsByCourseNameAndTeacherId(@Param("courseName") String courseName, @Param("teacherId") Integer teacherId);
    @Select("SELECT max_score, min_score, average_score " +
            "FROM course_score_analyse WHERE course_id = #{courseId}")
    Map<String, Object> getScoresByCourseId(@Param("courseId") Integer courseId);

//    纵向对比
    @Select("SELECT id, class_name, term_start, term_end " +
        "FROM course_basic_information WHERE course_name = #{courseName} AND teacher_id = #{teacherId} AND major = #{major}")
    List<CourseBasicInformation> getCourseIdsByCourseNameAndTeacherIdAndMajor(@Param("courseName") String courseName, @Param("teacherId") Integer teacherId, @Param("major") String major);
    @Select("SELECT max_score, min_score, average_score " +
            "FROM course_score_analyse WHERE course_id = #{courseId}")
    Map<String, Object> getScoresByCourseIdVer(@Param("courseId") Integer courseId);



}
