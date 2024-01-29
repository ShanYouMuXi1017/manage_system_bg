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
    @Select("SELECT id,name,teacher_name,is_admin,department,college_name from user;")
    List<User> getUser();

    @Select("SELECT is_admin from user group by is_admin;")
    List<User> getPower();

}
