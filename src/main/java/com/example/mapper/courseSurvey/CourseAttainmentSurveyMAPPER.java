package com.example.mapper.courseSurvey;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.object.courseSurvey.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CourseAttainmentSurveyMAPPER extends BaseMapper<CourseAttainmentSurvey> {
    //    在此出写拓展sql
    @Select("SELECT s.course_id,i.course_name FROM student_information s\n" +
            " JOIN course_basic_information i ON s.course_id = i.id\n" +
            "WHERE s.student_number = #{number} ")
    List<CoursePO> getCourseByStudentNumber(@Param("number") String number);

    @Select("SELECT c.course_id FROM course_attainment_survey c \n" +
            "WHERE c.student_number = #{number} GROUP BY c.course_id")
    List<Integer> isSurvey(@Param("number") String number);

    @Select("SELECT s.id as student_id, s.student_number, c.course_id,c.course_name,c.id AS courseTargetId,c.target_name,c.course_target,a.attainment \n" +
            "from student_information s JOIN course_target c ON s.course_id = c.course_id LEFT JOIN \n" +
            "course_attainment_survey a ON s.student_number = a.student_number AND c.id = a.course_target_id  \n" +
            "WHERE s.student_number = #{number} AND s.course_id=#{courseId}")
    List<SurveyPO> getSurvey(@Param("number") String number, @Param("courseId") Integer courseId);

    @Select("SELECT course_target_id,attainment,COUNT(attainment) AS amount\n" +
            "FROM course_attainment_survey  \n" +
            "WHERE course_id = #{courseId}\n" +
            "GROUP BY attainment,course_target_id")
    List<SurveyDAO> getTable(@Param("courseId") Integer courseId);

    @Select("SELECT s.course_target_id,COUNT(s.course_target_id) AS total,c.course_target\n" +
            "FROM course_attainment_survey s JOIN  course_target c ON s.course_target_id = c.id\n" +
            "WHERE s.course_id = #{courseId}\n" +
            "GROUP BY  s.course_target_id")
    List<SurveyVO> getTotal(@Param("courseId") Integer courseId);

    @Select("SELECT s.student_number,s.student_name,IF(COUNT(s.student_number)>1,1,0) AS completed FROM student_information s\n" +
            "LEFT JOIN course_attainment_survey c ON s.student_number = c.student_number\n" +
            "WHERE s.course_id = #{courseId} GROUP BY s.student_number,s.student_name ORDER BY completed,student_number")
    List<StudentVO> getStudent(@Param("courseId") Integer courseId);

}
