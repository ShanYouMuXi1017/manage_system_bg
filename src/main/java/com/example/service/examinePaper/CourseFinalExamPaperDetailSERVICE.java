package com.example.service.examinePaper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.object.finalExamine.CourseFinalExamPaperDetail;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface CourseFinalExamPaperDetailSERVICE extends IService<CourseFinalExamPaperDetail> {
    ResponseEntity<byte[]> ExportExamPaperRelationExcel(HttpServletResponse response,int courseId, int type) throws IOException;
}
