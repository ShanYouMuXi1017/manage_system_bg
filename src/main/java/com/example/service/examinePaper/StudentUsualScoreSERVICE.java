package com.example.service.examinePaper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.object.finalExamine.StudentUsualScore;
import com.example.utility.DataResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface StudentUsualScoreSERVICE extends IService<StudentUsualScore> {
    //mapper中添加的方法在此处声明
    //也可以通过@Override重写方法
    List<StudentUsualScore> getAllStudent(int courseId);

    List<String> getUsualExamMethods(int courseID);

    //学生平时总成绩设置和刷新
    void refreshStudentScore(int courseId);

    //学生成绩表格导出
    ResponseEntity<byte[]> exportStudentUsualScore(HttpServletResponse response, int courseId) throws IOException;

    @Transactional
    DataResponses inputStudentUsualScore(MultipartFile file, String courseId) throws IOException;

}
