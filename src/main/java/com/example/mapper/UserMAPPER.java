package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.object.College;
import com.example.object.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMAPPER extends BaseMapper<User> {

    //    在此出写拓展sql
    @Select("SELECT id,is_admin from user GROUP BY is_admin;")
    List<User> userPreList();

    @Select("SELECT id,college_name from college GROUP BY college_name;")
    List<College> userPrCollegeList();

    @Select("SELECT id,college_name,department_name from college GROUP BY department_name;")
    List<College> userDerList();

}
