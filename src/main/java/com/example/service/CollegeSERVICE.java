package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.object.College;
import com.example.object.LoginDTO;
import com.example.object.User;
import com.example.utility.DataResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface CollegeSERVICE extends IService<College> {

    List<College> getCollege();

    List<College> getDepartment();

 }
