package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.CourseBasicInformationMAPPER;
import com.example.object.CourseBasicInformation;
import com.example.service.CourseBasicInformationSERVICE;
import org.springframework.stereotype.Service;

@Service
public class CourseBasicInformationServiceIMPL extends ServiceImpl<CourseBasicInformationMAPPER, CourseBasicInformation> implements CourseBasicInformationSERVICE {
}
