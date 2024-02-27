package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.CollegeMAPPER;
import com.example.object.College;
import com.example.service.CollegeSERVICE;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollegeServiceIMPL extends ServiceImpl<CollegeMAPPER, College> implements CollegeSERVICE {


    private final CollegeMAPPER collegeMAPPER ;

    public CollegeServiceIMPL(CollegeMAPPER collegeMAPPER  ) {
        this.collegeMAPPER = collegeMAPPER;

    }

    @Override
    public List<College> getCollege(){
        return collegeMAPPER.getCollege();
    }

    @Override
    public List<College> getDepartment(){
        return collegeMAPPER.getDepartment();
    }


}

