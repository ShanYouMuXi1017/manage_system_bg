package com.example.mapper.examinePaper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.object.finalExamine.StudentComprehensiveScore;
import com.example.object.finalExamine.StudentInformation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface StudentInformationMAPPER extends BaseMapper<StudentInformation> {

    @Select("Select student_information.id,\n" +
            "       student_information.student_number,\n" +
            "       student_information.student_name,\n" +
            "       student_information.class_name,\n" +
            "       student_information.course_id,\n" +
            "       student_information.comprehensive_score,\n" +
            "       student_usual_score.score as usualScore,\n" +
            "       student_final_score.score as finalScore,\n" +
            "       student_final_score.score_details as ExamScore\n" +
            "from student_information\n" +
            "         left outer join student_usual_score on student_information.id = student_usual_score.student_id\n" +
            "         left outer join student_final_score on student_information.id = student_final_score.student_id\n" +
            "where student_information.course_id = #{courseId};")
    List<StudentComprehensiveScore> getComprehensiveScore(@Param("courseId") int courseId);


    @Update("update student_information set comprehensive_score = #{score} where id = #{studentId};")
    void UpdateComprehensiveScore(double score, int studentId);

    @Select("select count(id) from student_information where course_id = #{courseId};")
    int count(int courseId);

    @Select("select max(comprehensive_score) from student_information where course_id = #{courseId};")
    double max(int courseId);

    @Select("select min(comprehensive_score) from student_information where course_id = #{courseId};")
    double min(int courseId);

    @Select("select count(id) from student_information where course_id = #{courseId} && comprehensive_score >= 90;")
    int superior(int courseId);

    @Select("select count(id) from student_information where course_id = #{courseId} && comprehensive_score < 90 && comprehensive_score >= 80;")
    int great(int courseId);

    @Select("select count(id) from student_information where course_id = #{courseId} && comprehensive_score < 80 && comprehensive_score >= 70;")
    int good(int courseId);

    @Select("select count(id) from student_information where course_id = #{courseId} && comprehensive_score < 70 && comprehensive_score >= 60;")
    int pass(int courseId);

    @Select("select count(id) from student_information where course_id = #{courseId} && comprehensive_score >= 60;")
    int passNum(int courseId);

    @Select("select count(id) from student_information where course_id = #{courseId} && comprehensive_score < 60;")
    int failed(int courseId);

    @Select("select avg(comprehensive_score) from student_information where course_id = #{courseId};")
    double average(int courseId);

}
