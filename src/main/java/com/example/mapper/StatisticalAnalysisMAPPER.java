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
    @Select("SELECT id, class_name " +
            "FROM course_basic_information WHERE course_name = #{courseName} AND teacher_id = #{teacherId}")
    List<CourseBasicInformation> getCourseIdsByCourseNameAndTeacherId(@Param("courseName") String courseName, @Param("teacherId") Integer teacherId);

    @Select("SELECT max_score, min_score, average_score, " +
            "CASE WHEN student_num > 0 THEN superior * 1.0 / student_num * 100 ELSE 0 END AS superior_rate, " +
            "CASE WHEN student_num > 0 THEN great * 1.0 / student_num * 100 ELSE 0 END AS great_rate, " +
            "CASE WHEN student_num > 0 THEN good * 1.0 / student_num * 100 ELSE 0 END AS good_rate, " +
            "CASE WHEN student_num > 0 THEN pass * 1.0 / student_num * 100 ELSE 0 END AS pass_rates, " +
            "CASE WHEN student_num > 0 THEN failed * 1.0 / student_num * 100 ELSE 0 END AS failed_rate, " +
            "pass_rate " +
            "FROM course_score_analyse WHERE course_id = #{courseId}")
    Map<String, Object> getScoresByCourseId(@Param("courseId") Integer courseId);





//    //    纵向对比
//    @Select("SELECT id, class_name, term_start, term_end " +
//            "FROM course_basic_information WHERE course_name = #{courseName} AND teacher_id = #{teacherId} AND major = #{major}")
//    List<CourseBasicInformation> getCourseIdsByCourseNameAndTeacherIdAndMajor(@Param("courseName") String courseName, @Param("teacherId") Integer teacherId, @Param("major") String major);
//
//    @Select("SELECT max_score, min_score, average_score " +
//            "FROM course_score_analyse WHERE course_id = #{courseId}")
//    Map<String, Object> getScoresByCourseIdVer(@Param("courseId") Integer courseId);


}
