package com.example.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.object.CourseAllInformation;
import com.example.object.CourseExamineChildMethods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CourseExamineChildMethodsMAPPER extends BaseMapper<CourseExamineChildMethods> {
    //    在此出写拓展sql

}

