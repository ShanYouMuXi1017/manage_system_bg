package com.example.service.examinePaper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.object.finalExamine.StudentComprehensiveScore;
import com.example.object.finalExamine.StudentInformation;
import com.example.utility.DataResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface StudentInformationSERVICE extends IService<StudentInformation> {
    //学生信息导入
    @Transactional
    DataResponses inputStudentInfo(MultipartFile file, String courseId);

    //获取学生综合成绩
    List<StudentComprehensiveScore> getComprehensiveScore(int courseId);

    //导出学生综合成绩XLS
    ResponseEntity<byte[]> exportComprehensiveScore(HttpServletResponse response, int courseId);

    //导出达成度分析表
    ResponseEntity<byte[]> exportDegreeOfAchievement(int courseId,int type);

    //生成和刷新综合成绩
    void refreshScore(int courseId);

    //导出成绩分析PDF
    ResponseEntity<byte[]> exportComprehensiveScoreAnalyse(int courseId);

    //课程成绩分析
    void analyse(int courseId);
}
