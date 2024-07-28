package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.object.CourseBasicInformation;
import com.example.object.CourseSyllabusInformation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CourseBasicInformationMAPPER extends BaseMapper<CourseBasicInformation> {
    //    在此出写拓展sql
    @Update("update course_basic_information\n" +
            "set accomplish = 'true'\n" +
            "where id = #{courseId};")
    Boolean updateStatus(@Param("courseId") int courseId);


    @Select("SELECT major, course_name FROM course_syllabus_information ORDER BY major, course_name")
    List<CourseSyllabusInformation> getCourses();

    @Select("SELECT * FROM course_basic_information WHERE course_name = #{coursename}")
    List<CourseBasicInformation> courseMapper(@Param("coursename") String coursename);

    @Select("SELECT * FROM course_basic_information ORDER BY id DESC LIMIT 1 ")
    int getNewCourseId();
}
