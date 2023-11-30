package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.object.CourseBasicInformation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface CourseBasicInformationMAPPER extends BaseMapper<CourseBasicInformation> {
    //    在此出写拓展sql
    @Update("update course_basic_information\n" +
            "set accomplish = 'true'\n" +
            "where id = #{courseId};")
    Boolean updateStatus(@Param("courseId") int courseId);
}
