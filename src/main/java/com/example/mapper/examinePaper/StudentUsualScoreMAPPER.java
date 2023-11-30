package com.example.mapper.examinePaper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.object.finalExamine.StudentUsualScore;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StudentUsualScoreMAPPER extends BaseMapper<StudentUsualScore> {
    //    在此出写拓展sql
    @Select("Select student_information.id,\n" +
            "       student_information.student_number,\n" +
            "       student_information.student_name,\n" +
            "       student_information.class_name,\n" +
            "       student_information.course_id,\n" +
            "       student_usual_score.score_details,\n" +
            "       student_usual_score.usual_score_id,\n" +
            "       student_usual_score.score\n" +
            "from student_information\n" +
            "         left outer join student_usual_score on student_information.id = student_usual_score.student_id\n" +
            "where student_information.course_id = #{courseId};")
    List<StudentUsualScore> getAllStudent(@Param("courseId") int courseId);

    @Select("Select student_information.id,\n" +
            "       student_information.student_number,\n" +
            "       student_information.student_name,\n" +
            "       student_information.class_name,\n" +
            "       student_information.course_id,\n" +
            "       student_usual_score.score,\n" +
            "       student_usual_score.usual_score_id\n" +
            "from student_information\n" +
            "         left outer join student_usual_score on student_information.id = student_usual_score.student_id\n" +
            "where student_information.id = #{studentId}")
    StudentUsualScore getOneStudent(@Param("studentId") int studentId);

}
