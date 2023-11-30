package com.example.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.object.CourseAllInformation;
import com.example.object.CourseExamineChildMethods;
import com.example.object.CourseExamineMethods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CourseExamineMethodsMAPPER extends BaseMapper<CourseExamineMethods> {
    //    在此出写拓展sql
    @Select("select * from course_basic_information,course_examine_methods,course_examine_child_methods where course_basic_information.id = #{id} && course_examine_methods.course_id = course_basic_information.id && course_examine_child_methods.course_examine_methods_id = course_examine_methods.id;")
//    @Select("Select * from course_basic_information")
    List<CourseAllInformation> getAllInformation(@Param("id") int id);

    @Select("select course_examine_child_methods.id,\n" +
            "       course_examine_child_methods.course_examine_methods_id,\n" +
            "       course_examine_child_methods.examine_child_item,\n" +
            "       course_examine_child_methods.child_percentage,\n" +
            "       course_examine_child_methods.course_target,\n" +
            "       course_examine_child_methods.indicator_points_detail\n" +
            "from course_examine_methods\n" +
            "         right join course_examine_child_methods\n" +
            "                    on course_examine_methods.id = course_examine_child_methods.course_examine_methods_id\n" +
            "where course_examine_child_methods.examine_child_item = '试卷' and course_examine_methods.course_id = #{courseId}")
    CourseExamineChildMethods getExamPaperIdByCourseId(@Param("courseId") String courseId);
}

