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

public interface UserSERVICE extends IService<User> {
    DataResponses loginCheck(User user, HttpServletResponse response);
    //mapper中添加的方法在此处声明
    //也可以通过@Override重写方法



    DataResponses login(LoginDTO user);


    List<User> userPreList();

    List<College> userPrCollegeList();

    List<College> userDerList();

    //用户信息导入
    @Transactional
    DataResponses inputUserInfo(MultipartFile file);


    //导出模板XLS
    ResponseEntity<byte[]> exportUserInformation(HttpServletRequest request, HttpServletResponse response) throws IOException;

    //导出用户信息excel
    ResponseEntity<byte[]> outUserInformation(HttpServletRequest request, HttpServletResponse response) throws IOException ;
}
