package com.example.service.impl.examinePaper;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.CourseBasicInformationMAPPER;
import com.example.mapper.CourseExamineChildMethodsMAPPER;
import com.example.mapper.CourseExamineMethodsMAPPER;
import com.example.mapper.examinePaper.CourseFinalExamPaperDetailMAPPER;
import com.example.mapper.examinePaper.CourseFinalExamPaperMAPPER;
import com.example.mapper.examinePaper.StudentFinalScoreMAPPER;
import com.example.mapper.examinePaper.StudentInformationMAPPER;
import com.example.object.CourseBasicInformation;
import com.example.object.CourseExamineChildMethods;
import com.example.object.CourseExamineMethods;
import com.example.object.finalExamine.*;
import com.example.service.examinePaper.StudentFinalScoreSERVICE;
import com.example.utility.DataExtend;
import com.example.utility.DataResponses;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.example.utility.export.export;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

@Service
public class StudentFinalScoreServiceIMPL extends ServiceImpl<StudentFinalScoreMAPPER, StudentFinalScore> implements StudentFinalScoreSERVICE {
    //考核方式
    @Autowired
    private StudentInformationMAPPER studentInformationMAPPER;
    @Autowired
    private CourseExamineMethodsMAPPER courseExamineMethodsMAPPER;
    @Autowired
    private CourseExamineChildMethodsMAPPER courseExamineChildMethodsMAPPER;
    @Autowired
    private CourseFinalExamPaperMAPPER courseFinalExamPaperMAPPER;
    @Autowired
    private CourseFinalExamPaperDetailMAPPER courseFinalExamPaperDetailMAPPER;

    @Autowired
    private CourseBasicInformationMAPPER courseBasicInformationMAPPER;

    @Autowired
    private StudentFinalScoreMAPPER studentFinalScoreMAPPER;

    //获取试卷题型
    @Override
    public DataResponses getFinalExamPaper(int courseId) {
//        courseId = 10;
        List<DataExtend> strings = new ArrayList<>();

        QueryWrapper<CourseExamineMethods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", courseId);
        queryWrapper.like("examine_item", "期末");
        CourseExamineMethods methods = courseExamineMethodsMAPPER.selectOne(queryWrapper);
        if (methods == null) {
            return new DataResponses(true, null);
        }
        Integer methodsId = methods.getId();

        QueryWrapper<CourseExamineChildMethods> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.eq("course_examine_methods_id", methodsId);
        queryWrapper2.like("examine_child_item", "试卷");
        CourseExamineChildMethods courseExamineChildMethods = courseExamineChildMethodsMAPPER.selectOne(queryWrapper2);
        Integer paperId = courseExamineChildMethods.getId();

        QueryWrapper<CourseFinalExamPaper> queryWrapper3 = new QueryWrapper<>();
        queryWrapper3.eq("exam_child_method_id", paperId);
        List<CourseFinalExamPaper> courseFinalExamPapers = courseFinalExamPaperMAPPER.selectList(queryWrapper3);
        for (CourseFinalExamPaper courseFinalExamPaper : courseFinalExamPapers) {
            String itemName = courseFinalExamPaper.getItemName() + "( " + courseFinalExamPaper.getItemScore() + " )";
            Integer id = courseFinalExamPaper.getId();

            QueryWrapper<CourseFinalExamPaperDetail> queryWrapper4 = new QueryWrapper<>();
            queryWrapper4.eq("primary_id", id);
            queryWrapper4.orderByAsc("title_number");
            List<CourseFinalExamPaperDetail> courseFinalExamPaperDetails = courseFinalExamPaperDetailMAPPER.selectList(queryWrapper4);
            List<Integer> strings2 = new ArrayList<>();
            for (CourseFinalExamPaperDetail courseFinalExamPaperDetail : courseFinalExamPaperDetails) {
                strings2.add(courseFinalExamPaperDetail.getTitleNumber());
            }
            strings.add(new DataExtend(itemName, strings2.toString()));

        }

        return new DataResponses(true, strings);
    }

    //获取学生期末成绩
    @Override
    public DataResponses getAllStudent(int courseId) {
        refreshStudentScore(courseId);
        List<StudentFinalScore> allStudent = studentFinalScoreMAPPER.getAllStudent(courseId);
        DataResponses finalExamPaper = getFinalExamPaper(courseId);

        for (StudentFinalScore studentFinalScore : allStudent) {
            List<List<String>> emptyArray = new ArrayList<>();
            if (studentFinalScore.getScoreDetails() == null) {
                List<DataExtend> data = (List<DataExtend>) finalExamPaper.getData();
                if (data == null) {
                    break;
                }
                for (DataExtend item : data) {
                    List<String> emptyStringList = new ArrayList<>();
                    JSONArray jsonArray = JSONArray.parseArray(item.getData());
                    for (Object i : jsonArray) {
                        emptyStringList.add("");
                    }
                    emptyArray.add(emptyStringList);
                }
                studentFinalScore.setScoreResponse(emptyArray);
            } else {
                studentFinalScore.setScoreResponse(JSONArray.parseArray(studentFinalScore.getScoreDetails()));
            }
        }
        return new DataResponses(true, allStudent);
    }

    //学生期末成绩生成刷新
    @Override
    public void refreshStudentScore(int courseId) {
        List<StudentFinalScore> allStudent = studentFinalScoreMAPPER.getAllStudent(courseId);
        for (StudentFinalScore score : allStudent) {
            if (score.getFinalScoreId() != null) {
                StudentFinalScore finalScore = new StudentFinalScore();
                finalScore.setFinalScoreId(score.getFinalScoreId());
                finalScore.setStudentId(score.getId());
                finalScore.setScoreDetails(score.getScoreDetails());

                double sum = 0;
                List<List<String>> lists = export.stringTo2DArray(score.getScoreDetails());
                for (List<String> list : lists) {
                    for (String s : list) {
                        if (Objects.equals(s, "") || Objects.equals(s, " ")) {
                            s = "0";
                        }
                        sum += Double.parseDouble(s);
                    }
                }
                DecimalFormat decimalFormat = new DecimalFormat("#.0");
                double formattedNumber = Double.parseDouble(decimalFormat.format(sum));
                finalScore.setScore(formattedNumber);

                QueryWrapper<StudentFinalScore> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("final_score_id", finalScore.getFinalScoreId());
                studentFinalScoreMAPPER.update(finalScore, queryWrapper);
            }
        }
    }

    //导出学生期末成绩
    @Override
    public ResponseEntity<byte[]> exportStudentFinalScore(HttpServletResponse response, int courseId) throws IOException {

        //行列索引
        int rowIndex = 1;
        int columIndex = 0;

        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        //单元格样式
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        //表头设置
        Row row1 = sheet.createRow(0);
        row1.setRowStyle(style);
        Row row2 = sheet.createRow(1);
        row2.setRowStyle(style);
        Row row3 = sheet.createRow(2);
        row3.setRowStyle(style);
        Row row4 = sheet.createRow(3);
        row4.setRowStyle(style);

        CellRangeAddress mergedRegion = new CellRangeAddress(1, 3, 0, 0);
        sheet.addMergedRegion(mergedRegion);
        row2.createCell(0).setCellValue("学号");
        export.reloadCellStyle(mergedRegion, sheet, style);
        sheet.setColumnWidth(0, 20 * 256);

        mergedRegion = new CellRangeAddress(1, 3, 1, 1);
        sheet.addMergedRegion(mergedRegion);
        row2.createCell(1).setCellValue("姓名");
        export.reloadCellStyle(mergedRegion, sheet, style);
        sheet.autoSizeColumn(1);

        mergedRegion = new CellRangeAddress(1, 3, 2, 2);
        sheet.addMergedRegion(mergedRegion);
        row2.createCell(2).setCellValue("班级");
        export.reloadCellStyle(mergedRegion, sheet, style);
        sheet.setColumnWidth(2, 20 * 256);

        //获取试卷题型
        columIndex = 3;
        int temp = columIndex;
        DataResponses result = getFinalExamPaper(courseId);
        List<DataExtend> finalExamPaper = (List<DataExtend>) result.getData();
        for (DataExtend data : finalExamPaper) {
            JSONArray objects = JSONArray.parseArray(data.getData());
            for (Object object : objects) {
                row4.createCell(columIndex).setCellValue((int) object);
                row4.getCell(columIndex).setCellStyle(style);
                sheet.setColumnWidth(columIndex, 5 * 256);
                columIndex++;
            }
            columIndex--;
            CellRangeAddress cellAddresses = new CellRangeAddress(2, 2, temp, columIndex);
            if (temp != columIndex) {
                sheet.addMergedRegion(cellAddresses);
            }
            row3.createCell(temp).setCellValue(data.getMessage());
            export.reloadCellStyle(cellAddresses, sheet, style);
            columIndex++;
            temp = columIndex;
        }

        mergedRegion = new CellRangeAddress(1, 1, 3, columIndex);
        sheet.addMergedRegion(mergedRegion);
        row2.createCell(3).setCellValue("试卷");
        export.reloadCellStyle(mergedRegion, sheet, style);

        mergedRegion = new CellRangeAddress(2, 3, columIndex, columIndex);
        sheet.addMergedRegion(mergedRegion);
        row3.createCell(columIndex).setCellValue("总分");
        export.reloadCellStyle(mergedRegion, sheet, style);

        //标题
        mergedRegion = new CellRangeAddress(0, 0, 0, columIndex);
        sheet.addMergedRegion(mergedRegion);
        CourseBasicInformation courseBasicInformation = courseBasicInformationMAPPER.selectById(courseId);
        String s = courseBasicInformation.getCourseName() + " ( " + courseBasicInformation.getTermStart() + " - " + courseBasicInformation.getTermEnd() + "." + courseBasicInformation.getTerm() + " ) " + "\t" + courseBasicInformation.getClassName() + "\t" + courseBasicInformation.getClassroomTeacher();
        row1.createCell(0).setCellValue(s);
        export.reloadCellStyle(mergedRegion, sheet, style);

        //绘制学生信息
        rowIndex = 4;
        DataResponses responses = getAllStudent(courseId);
        List<StudentFinalScore> allStudent = (List<StudentFinalScore>) responses.getData();
        for (StudentFinalScore studentFinalScore : allStudent) {
            List<List<String>> lists = export.stringTo2DArray(studentFinalScore.getScoreDetails());
            export.valueToCell(sheet, rowIndex, 0, studentFinalScore.getStudentNumber(), style);
            export.valueToCell(sheet, rowIndex, 1, studentFinalScore.getStudentName(), style);
            export.valueToCell(sheet, rowIndex, 2, studentFinalScore.getClassName(), style);

            int index = 3;
            if (lists != null) {
                for (List<String> row : lists) {
                    for (String value : row) {
                        if (!Objects.equals(value, "null")) {
                            export.valueToCell(sheet, rowIndex, index, value, style);
                        }
                        index++;
                    }
                }
            }

            rowIndex++;
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        workbook.write(byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();

        String fileName = courseBasicInformation.getTermStart() + " - " + courseBasicInformation.getTermEnd() + "学年第" + courseBasicInformation.getTerm() + "学期" + courseBasicInformation.getClassName() + courseBasicInformation.getCourseName() + "[" + courseBasicInformation.getClassroomTeacher() + "]" + "卷面成绩.xls";

        response.reset();
        response.setContentType("application/vnd.ms-excel");

        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Disposition", "attachment;fileName=" + new String(fileName.getBytes(), "iso-8859-1"));

        return ResponseEntity.ok()
                .body(bytes);
    }

    //学生期末成绩导入
    @Override
    @Transactional
    public DataResponses inputStudentFinalScore(MultipartFile file, int courseId) {
        try {

            String fileSuffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            Workbook workbook = null;

            if (fileSuffix.equals(".xls")) {
                workbook = new HSSFWorkbook(file.getInputStream());
            } else if (fileSuffix.equals(".xlsx")) {
                workbook = new XSSFWorkbook(file.getInputStream());
            }
            assert workbook != null;
            Sheet sheet = workbook.getSheetAt(0);

//            DataFormatter formatter = new DataFormatter();
            //遍历行
            for (int i = 4; i < sheet.getLastRowNum() + 1; i++) {
                Row row = sheet.getRow(i);
//                StudentInformation information = new StudentInformation(formatter.formatCellValue(row.getCell(0)), formatter.formatCellValue(row.getCell(1)), formatter.formatCellValue(row.getCell(2)), courseId);
                QueryWrapper<StudentInformation> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("student_number", export.getCellStringValue(row.getCell(0)));
                queryWrapper.eq("course_id", courseId);
                StudentInformation information = studentInformationMAPPER.selectOne(queryWrapper);

                StudentInformation student = new StudentInformation();
                student.setStudentName(export.getCellStringValue(row.getCell(1)));
                student.setClassName(export.getCellStringValue(row.getCell(2)));

                Integer id = 0;
                if (information == null) {
                    student.setStudentNumber(export.getCellStringValue(row.getCell(0)));
                    student.setCourseId(String.valueOf(courseId));

                    studentInformationMAPPER.insert(student);
                    id = studentInformationMAPPER.selectOne(queryWrapper).getId();
                } else {
                    id = information.getId();
                    student.setId(id);

                    studentInformationMAPPER.updateById(student);
                }


                DataResponses result = getFinalExamPaper(courseId);
                List<DataExtend> finalExamPaper = (List<DataExtend>) result.getData();
                int index = 3;
                List<List> list = new ArrayList();
                for (DataExtend data : finalExamPaper) {
                    JSONArray objects = JSONArray.parseArray(data.getData());
                    List<String> list1 = new ArrayList();
                    for (int j = 0; j < objects.size(); j++) {
                        String cellStringValue = export.getCellStringValue(row.getCell(index));
                        if (!export.isNumeric(cellStringValue)){
                            list1.add("");
                        }else{
                            list1.add(cellStringValue);
                        }
                        index++;
                    }
                    list.add(list1);
                }

                String s = JSON.toJSONString(list);
                StudentFinalScore score = new StudentFinalScore();
                score.setStudentId(id);
                score.setScoreDetails(s);

                QueryWrapper<StudentFinalScore> queryWrapper2 = new QueryWrapper<>();
                queryWrapper2.eq("student_id", id);

                if (studentFinalScoreMAPPER.getOneStudent(id).getFinalScoreId() == null) {
                    studentFinalScoreMAPPER.insert(score);
                } else {
                    studentFinalScoreMAPPER.update(score, queryWrapper2);
                }
            }
            refreshStudentScore(courseId);
            return new DataResponses(true, "导入成功");

        } catch (IOException exception) {
            return new DataResponses(false, "导入失败，表格数据有缺失");
        }
    }

}
