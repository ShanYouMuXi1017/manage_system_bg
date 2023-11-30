package com.example.mapper.comprehensiveAnalyse;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.object.comprehensiveAnalyse.CourseTargetAnalyse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CourseTargetAnalyseMAPPER extends BaseMapper<CourseTargetAnalyse> {

    @Select("Select avg(student_usual_score.score)\n" +
            "from student_information\n" +
            "         left outer join student_usual_score on student_information.id = student_usual_score.student_id\n" +
            "where student_information.course_id = #{courseId};")
    double getUsualScoreAVG(@Param("courseId") int courseId);

    @Select("Select avg(student_final_score.score)\n" +
            "from student_information\n" +
            "         left outer join student_final_score on student_information.id = student_final_score.student_id\n" +
            "where student_information.course_id = #{courseId};")
    double getFinalScoreAVG(@Param("courseId") int courseId);

    @Select("select avg(comprehensive_score) from student_information where course_id = #{courseId};")
    double getComprehensiveAverage(int courseId);
}
