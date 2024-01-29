package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.object.College;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CollegeMAPPER extends BaseMapper<College> {

    @Select("SELECT  college_name from college GROUP BY college_name;")
    List<College> getCollege();

    @Select("SELECT  department_name from college GROUP BY department_name;")
    List<College> getDepartment();

}
