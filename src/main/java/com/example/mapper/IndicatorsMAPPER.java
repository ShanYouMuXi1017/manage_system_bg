package com.example.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.object.Indicators;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface IndicatorsMAPPER extends BaseMapper<Indicators> {
//    在此出写拓展sql

    @Select("select * from indicators where major like #{major}")
    List<Indicators> getMajorIndicators(@Param("major")String major);
}
