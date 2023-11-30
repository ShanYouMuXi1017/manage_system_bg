package com.example.service.examinePaper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.object.finalExamine.StudentFinalScore;
import com.example.utility.DataResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface StudentFinalScoreSERVICE extends IService<StudentFinalScore> {
    DataResponses getFinalExamPaper(int courseId);

    DataResponses getAllStudent(int courseId);

    //学生期末成绩生成刷新
    void refreshStudentScore(int course);

    //导出学生期末成绩
    ResponseEntity<byte[]> exportStudentFinalScore(HttpServletResponse response, int courseId) throws IOException;

    //学生平时成绩导入
    DataResponses inputStudentFinalScore(MultipartFile file, int courseId);
}
