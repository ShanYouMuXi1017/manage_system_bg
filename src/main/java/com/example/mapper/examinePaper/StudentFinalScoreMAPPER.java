package com.example.mapper.examinePaper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.object.finalExamine.StudentFinalScore;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StudentFinalScoreMAPPER extends BaseMapper<StudentFinalScore> {
    @Select("Select student_information.id,\n" +
            "       student_information.student_number,\n" +
            "       student_information.student_name,\n" +
            "       student_information.class_name,\n" +
            "       student_information.course_id,\n" +
            "       student_final_score.score,\n" +
            "       student_final_score.final_score_id,\n" +
            "       student_final_score.student_id,\n" +
            "       student_final_score.score_details \n" +
            "from student_information\n" +
            "         left outer join student_final_score on student_information.id = student_final_score.student_id\n" +
            "where student_information.course_id = #{courseId}")
    List<StudentFinalScore> getAllStudent(@Param("courseId") int courseId);


    @Select("Select student_information.id,\n" +
            "       student_information.student_number,\n" +
            "       student_information.student_name,\n" +
            "       student_information.class_name,\n" +
            "       student_information.course_id,\n" +
            "       student_final_score.score,\n" +
            "       student_final_score.final_score_id\n" +
            "from student_information\n" +
            "         left outer join student_final_score on student_information.id = student_final_score.student_id\n" +
            "where student_information.id = #{studentId}")
    StudentFinalScore getOneStudent(@Param("studentId") int studentId);
}
