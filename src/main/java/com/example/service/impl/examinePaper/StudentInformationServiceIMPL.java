package com.example.service.impl.examinePaper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.CourseBasicInformationMAPPER;
import com.example.mapper.CourseExamineMethodsMAPPER;
import com.example.mapper.comprehensiveAnalyse.CourseAchievementAnalyseMAPPER;
import com.example.mapper.comprehensiveAnalyse.CourseScoreAnalyseMAPPER;
import com.example.mapper.comprehensiveAnalyse.CourseTargetAnalyseMAPPER;
import com.example.mapper.examinePaper.StudentInformationMAPPER;
import com.example.object.CourseBasicInformation;
import com.example.object.CourseExamineMethods;
import com.example.object.comprehensiveAnalyse.CourseAchievementAnalyse;
import com.example.object.comprehensiveAnalyse.CourseScoreAnalyse;
import com.example.object.comprehensiveAnalyse.CourseTargetAnalyse;
import com.example.object.comprehensiveAnalyse.KeyValue;
import com.example.object.finalExamine.StudentComprehensiveScore;
import com.example.object.finalExamine.StudentInformation;
import com.example.service.examinePaper.StudentInformationSERVICE;
import com.example.utility.DataResponses;
import com.example.utility.export.export;
import com.spire.xls.FileFormat;
import org.apache.poi.hssf.OldExcelFormatException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class StudentInformationServiceIMPL extends ServiceImpl<StudentInformationMAPPER, StudentInformation> implements StudentInformationSERVICE {

    @Autowired
    private StudentInformationMAPPER studentInformationMAPPER;

    @Autowired
    private CourseExamineMethodsMAPPER courseExamineMethodsMAPPER;

    @Autowired
    private CourseScoreAnalyseMAPPER courseScoreAnalyseMAPPER;

    @Autowired
    private CourseTargetAnalyseMAPPER courseTargetAnalyseMAPPER;

    @Autowired
    private CourseBasicInformationMAPPER courseBasicInformationMAPPER;

    @Autowired
    private CourseAchievementAnalyseMAPPER courseAchievementAnalyseMAPPER;

    //学生信息导入
    @Override
    @Transactional
    public DataResponses inputStudentInfo(MultipartFile file, String courseId) {
        try {
            String fileSuffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            Workbook workbook = null;
            DataFormatter formatter = new DataFormatter();

            if (fileSuffix.equals(".xls")) {
                try{
                    workbook = new HSSFWorkbook(file.getInputStream());
                }catch (OldExcelFormatException e){
                    return new DataResponses(false, "该表格生成时采用的模板是 Excel 5.0/95工作簿 版本太老不兼容，请将表格在本地另存为操作一下即可导入");
                }
            } else if (fileSuffix.equals(".xlsx")) {
                workbook = new XSSFWorkbook(file.getInputStream());
            }
            assert workbook != null;
            Sheet sheet = workbook.getSheetAt(0);

            //对应的列位置索引
            int StudentNumber = 0;
            int StudentName = 0;
            int ClassName = 0;
            int i = 0;

            Row row1 = sheet.getRow(0);
            while (true) {
                if (row1.getCell(i) != null) {
                    String s = formatter.formatCellValue(row1.getCell(i));
                    if (Objects.equals(s, "")) {
                        break;
                    }
                    switch (s) {
                        case "学号":
                            StudentNumber = i;
                            break;
                        case "姓名":
                            StudentName = i;
                            break;
                        case "班级":
                            ClassName = i;
                            break;
                    }
                    i++;
                } else {
                    break;
                }
            }
            //遍历行
            for (int rowIndex = 1; rowIndex < sheet.getLastRowNum() + 1; rowIndex++) {
                Row row = sheet.getRow(rowIndex);

                QueryWrapper<StudentInformation> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("student_number", formatter.formatCellValue(row.getCell(StudentNumber)));
                queryWrapper.eq("course_id", courseId);
                StudentInformation information = studentInformationMAPPER.selectOne(queryWrapper);

                StudentInformation student = new StudentInformation();
                student.setStudentName(formatter.formatCellValue(row.getCell(StudentName)));
                student.setClassName(formatter.formatCellValue(row.getCell(ClassName)));

                Integer id = 0;
                if (information == null) {
                    student.setStudentNumber(formatter.formatCellValue(row.getCell(StudentNumber)));
                    student.setCourseId(courseId);

                    studentInformationMAPPER.insert(student);
                } else {
                    id = information.getId();
                    student.setId(id);

                    studentInformationMAPPER.updateById(student);
                }
            }


            return new DataResponses(true, "导入成功");

        } catch (IOException exception) {
            return new DataResponses(false, "导入失败，表格数据有缺失");
        }
    }


    //获取学生综合成绩
    @Override
    public List<StudentComprehensiveScore> getComprehensiveScore(int courseId) {
        refreshScore(courseId);
        analyse(courseId);
        return studentInformationMAPPER.getComprehensiveScore(courseId);
    }

    //导出学生综合成绩XLS
    @Override
    public ResponseEntity<byte[]> exportComprehensiveScore(HttpServletResponse response, int courseId) {
        try {
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

            Row row1 = sheet.createRow(0);

            export.valueToCell(sheet, 1, 0, "班级", style);
            sheet.setColumnWidth(0, 20 * 256);
            export.valueToCell(sheet, 1, 1, "姓名", style);
            export.valueToCell(sheet, 1, 2, "学号", style);
            sheet.setColumnWidth(2, 20 * 256);
            export.valueToCell(sheet, 1, 3, "平时成绩", style);
            export.valueToCell(sheet, 1, 4, "期末成绩", style);
            export.valueToCell(sheet, 1, 5, "总成绩", style);

            List<StudentComprehensiveScore> comprehensiveScore = studentInformationMAPPER.getComprehensiveScore(courseId);

            int rowIndex = 2;
            for (StudentComprehensiveScore score : comprehensiveScore) {
                export.valueToCell(sheet, rowIndex, 0, score.getClassName(), style);
                export.valueToCell(sheet, rowIndex, 1, score.getStudentName(), style);
                export.valueToCell(sheet, rowIndex, 2, score.getStudentNumber(), style);
                export.valueToCell(sheet, rowIndex, 3, String.valueOf(score.getUsualScore()), style);
                export.valueToCell(sheet, rowIndex, 4, String.valueOf(score.getFinalScore()), style);
                export.valueToCell(sheet, rowIndex, 5, String.valueOf(score.getComprehensiveScore()), style);
                rowIndex++;
            }

            //标题
            CellRangeAddress mergedRegion = new CellRangeAddress(0, 0, 0, 5);
            sheet.addMergedRegion(mergedRegion);
            CourseBasicInformation courseBasicInformation = courseBasicInformationMAPPER.selectById(courseId);
            String s = courseBasicInformation.getCourseName() + " ( " + courseBasicInformation.getTermStart() + " - " + courseBasicInformation.getTermEnd() + "." + courseBasicInformation.getTerm() + " ) " + "\t" + courseBasicInformation.getClassName() + "\t" + courseBasicInformation.getClassroomTeacher();
            row1.createCell(0).setCellValue(s);
            export.reloadCellStyle(mergedRegion, sheet, style);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            workbook.write(byteArrayOutputStream);
            byte[] bytes = byteArrayOutputStream.toByteArray();

            String fileName = courseBasicInformation.getTermStart() + " - " + courseBasicInformation.getTermEnd() + "学年第" + courseBasicInformation.getTerm() + "学期" + courseBasicInformation.getClassName() + courseBasicInformation.getCourseName() + "[" + courseBasicInformation.getClassroomTeacher() + "]" + "学生综合成绩.xls";

            response.reset();
            response.setContentType("application/vnd.ms-excel");

            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-Disposition", "attachment;fileName=" + new String(fileName.getBytes(), "iso-8859-1"));

            return ResponseEntity.ok()
                    .body(bytes);

        } catch (IOException ignored) {
        }
        return null;
    }

    //导出达成度分析表
    @Override
    public ResponseEntity<byte[]> exportDegreeOfAchievement(int courseId, int type) {
        try {
            Workbook workbook = new HSSFWorkbook();
            Sheet sheet = workbook.createSheet();
            DataFormatter formatter = new DataFormatter();

            //单元格样式
            CellStyle style = workbook.createCellStyle();
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);

            Row row1 = sheet.createRow(0);

            export.valueToCell(sheet, 1, 0, "学号", style);
            sheet.setColumnWidth(0, 20 * 256);
            export.valueToCell(sheet, 1, 1, "姓名", style);
            export.valueToCell(sheet, 1, 2, "班级", style);
            sheet.setColumnWidth(2, 20 * 256);
            export.valueToCell(sheet, 1, 3, "平时成绩", style);
            export.valueToCell(sheet, 1, 4, "期末成绩", style);
            export.valueToCell(sheet, 1, 5, "总成绩", style);
            export.valueToCell(sheet, 1, 6, "平时观测点达成度", style);
            sheet.setColumnWidth(6, 20 * 256);

            QueryWrapper<CourseTargetAnalyse> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("course_id", courseId);
            queryWrapper.like("target_name", "课程目标");
            queryWrapper.orderByAsc("target_name");
            int columIndex = 7;
            List<CourseTargetAnalyse> courseTargetAnalyses = courseTargetAnalyseMAPPER.selectList(queryWrapper);
            for (CourseTargetAnalyse targetAnalyse : courseTargetAnalyses) {
                int temp = columIndex;
                Row row = sheet.getRow(0);
                row.createCell(columIndex).setCellValue(targetAnalyse.getTargetName());
                export.valueToCell(sheet, 1, columIndex, "试卷", style);

                row.createCell(++columIndex);
                export.valueToCell(sheet, 1, columIndex, "试卷观测点达成度", style);
                sheet.setColumnWidth(columIndex, 20 * 256);

                row.createCell(++columIndex);
                export.valueToCell(sheet, 1, columIndex, "课程目标达成度", style);
                sheet.setColumnWidth(columIndex, 20 * 256);

                CellRangeAddress mergedRegion = new CellRangeAddress(0, 0, temp, columIndex);
                sheet.addMergedRegion(mergedRegion);
                export.reloadCellStyle(mergedRegion, sheet, style);

                columIndex++;
                temp = columIndex;
            }

            //获取基本数据
            List<StudentComprehensiveScore> comprehensiveScore = studentInformationMAPPER.getComprehensiveScore(courseId);
            QueryWrapper<CourseExamineMethods> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.eq("course_id", courseId);
            queryWrapper2.orderByAsc("examine_item");
            List<CourseExamineMethods> courseExamineMethods = courseExamineMethodsMAPPER.selectList(queryWrapper2);
            double percentage1 = 0;
            double percentage2 = 0;
            double percentage3 = 0;
            for (CourseExamineMethods methods : courseExamineMethods) {
                if (methods.getExamineItem().contains("实验")) percentage1 = methods.getPercentage() * 0.01;
                if (methods.getExamineItem().contains("平时")) percentage2 = methods.getPercentage() * 0.01;
                if (methods.getExamineItem().contains("期末")) percentage3 = methods.getPercentage() * 0.01;
            }

            int rowIndex = 2;
            double usualAchievementAVG = 0;
            for (StudentComprehensiveScore score : comprehensiveScore) {
                export.valueToCell(sheet, rowIndex, 0, score.getStudentNumber(), style);
                export.valueToCell(sheet, rowIndex, 1, score.getStudentName(), style);
                export.valueToCell(sheet, rowIndex, 2, score.getClassName(), style);
                export.valueToCell(sheet, rowIndex, 3, String.valueOf(score.getUsualScore()), style);
                export.valueToCell(sheet, rowIndex, 4, String.valueOf(score.getFinalScore()), style);
                export.valueToCell(sheet, rowIndex, 5, String.valueOf(score.getComprehensiveScore()), style);

                //平时观测点达成度
                double usualAchievement = export.doubleFormat(score.getUsualScore() * 0.01, 4);
                export.valueToCell(sheet, rowIndex, 6, String.valueOf(usualAchievement), style);
                usualAchievementAVG += usualAchievement;

                List<Double> ints = new ArrayList<>();
                List<List<String>> lists = export.stringTo2DArray(score.getExamScore());
                if (lists != null) {
                    for (List<String> l : lists) {
                        for (String s : l) {
                            if (Objects.equals(s, "")) {
                                ints.add(0.0);
                            } else {
                                double i = Double.parseDouble(s);
                                ints.add(i);
                            }
                        }
                    }
                }
                int i = 7;
                for (CourseTargetAnalyse targetAnalyse : courseTargetAnalyses) {
                    JSONArray objects = JSONArray.parseArray(targetAnalyse.getMatrix());
                    double result = 0;
                    for (int j = 0; j < ints.size(); j++) {
                        if (Boolean.parseBoolean(objects.get(j).toString())) {
                            result += ints.get(j);
                        }
                    }
                    double achievement = export.doubleFormat(result / targetAnalyse.getValue(), 4);
                    double targetAchievement = export.doubleFormat(usualAchievement * percentage2 + achievement * percentage3, 4);

                    //试卷分值对应
                    export.valueToCell(sheet, rowIndex, i, String.valueOf(result), style);
                    //试卷观测点达成度
                    export.valueToCell(sheet, rowIndex, i + 1, String.valueOf(achievement), style);
                    //课程目标达成度
                    export.valueToCell(sheet, rowIndex, i + 2, String.valueOf(targetAchievement), style);

                    i += 3;
                }
                rowIndex++;
            }

            //标题
            CellRangeAddress mergedRegion = new CellRangeAddress(0, 0, 0, 5);
            sheet.addMergedRegion(mergedRegion);
            CourseBasicInformation courseBasicInformation = courseBasicInformationMAPPER.selectById(courseId);
            String s = courseBasicInformation.getCourseName() + " ( " + courseBasicInformation.getTermStart() + " - " + courseBasicInformation.getTermEnd() + "." + courseBasicInformation.getTerm() + " ) " + "\t" + courseBasicInformation.getClassName() + "\t" + courseBasicInformation.getClassroomTeacher();
            row1.createCell(0).setCellValue(s);
            export.reloadCellStyle(mergedRegion, sheet, style);

            export.valueToCell(sheet, rowIndex, 0, "平均值", style);
            double v1 = export.doubleFormat(courseTargetAnalyseMAPPER.getUsualScoreAVG(courseId), 2);
            export.valueToCell(sheet, rowIndex, 3, String.valueOf(v1), style);
            double v2 = export.doubleFormat(courseTargetAnalyseMAPPER.getFinalScoreAVG(courseId), 2);
            export.valueToCell(sheet, rowIndex, 4, String.valueOf(v2), style);
            double v3 = export.doubleFormat(courseTargetAnalyseMAPPER.getComprehensiveAverage(courseId), 2);
            export.valueToCell(sheet, rowIndex, 5, String.valueOf(v3), style);
            double v4 = export.doubleFormat(usualAchievementAVG / (rowIndex - 3), 2);
            export.valueToCell(sheet, rowIndex, 6, String.valueOf(v4), style);

            //求平均值
            for (int i = 7; i < columIndex; i++) {
                double result = 0;
                int index = 0;
                for (int j = 2; j < rowIndex; j++) {
                    String s1 = formatter.formatCellValue(sheet.getRow(j).getCell(i));
                    if (!Objects.equals(s1, "")) {
                        double v = Double.parseDouble(s1);
                        result += v;
                    }
                    index++;
                }
                result = result / index;
                double v = export.doubleFormat(result, 4);
                export.valueToCell(sheet, rowIndex, i, String.valueOf(v), style);
            }

            //这代码狗见了都摇头
            //持久化存储分析数据
            List<List<Object>> analyseData = new ArrayList<>();
            for (int i = 9; i < columIndex; i += 3) {
                List<Object> list = new ArrayList<>();

                int index = 1;
                for (int j = 2; j < rowIndex; j++) {
                    KeyValue value = new KeyValue();
                    value.setIndex(index);
                    value.setValue(Double.parseDouble(sheet.getRow(j).getCell(i).getStringCellValue()));

                    list.add(value);
                    index++;
                }
                analyseData.add(list);
            }
            String s1 = JSON.toJSONString(analyseData);
            CourseAchievementAnalyse courseAchievementAnalyse = new CourseAchievementAnalyse();
            courseAchievementAnalyse.setCourseId(courseId);
            courseAchievementAnalyse.setValue(s1);

            QueryWrapper<CourseAchievementAnalyse> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("course_id", courseId);
            if (courseAchievementAnalyseMAPPER.update(courseAchievementAnalyse, queryWrapper1) != 1) {
                courseAchievementAnalyseMAPPER.insert(courseAchievementAnalyse);
            }

            ByteArrayOutputStream outputStreamXLS = new ByteArrayOutputStream();
            workbook.write(outputStreamXLS);

            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStreamXLS.toByteArray());

            // 加载 XLS 文件
            com.spire.xls.Workbook workbookn = new com.spire.xls.Workbook();
            workbookn.loadFromStream(inputStream);
            //设置转换后的PDGF页面高度适应工作表的内容大小
            workbookn.getConverterSetting().setSheetFitToPage(true);
            //设置转换后PDF的页面宽度适应工作表的内容宽度
            workbookn.getConverterSetting().setSheetFitToWidth(true);
            // 将 XLS 文件转换为 PDF 文件
            ByteArrayOutputStream outputStreamPDF = new ByteArrayOutputStream();
            workbookn.saveToStream(outputStreamPDF, FileFormat.PDF);
            // 将 PDF 文件读入字节数组
            byte[] Bytes = null;
            HttpHeaders headers = new HttpHeaders();
            if (type == 1) {
                Bytes = outputStreamPDF.toByteArray();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=converted.pdf");
                headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);
            } else if (type == 2) {
                Bytes = outputStreamXLS.toByteArray();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=template.xls");
                headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
            }
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(Bytes);
        } catch (IOException ignored) {
        }
        return null;
    }

    //生成和刷新综合成绩
    @Override
    public void refreshScore(int courseId) {
        List<StudentComprehensiveScore> comprehensiveScore = studentInformationMAPPER.getComprehensiveScore(courseId);
        QueryWrapper<CourseExamineMethods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", courseId);
        queryWrapper.orderByAsc("examine_item");
        List<CourseExamineMethods> courseExamineMethods = courseExamineMethodsMAPPER.selectList(queryWrapper);
        double percentage1 = 0;
        double percentage2 = 0;
        double percentage3 = 0;
        for (CourseExamineMethods methods : courseExamineMethods) {
            if (methods.getExamineItem().contains("实验")) percentage1 = methods.getPercentage() * 0.01;
            if (methods.getExamineItem().contains("平时")) percentage2 = methods.getPercentage() * 0.01;
            if (methods.getExamineItem().contains("期末")) percentage3 = methods.getPercentage() * 0.01;
        }

        for (StudentComprehensiveScore score : comprehensiveScore) {
            if (!Double.isNaN(score.getUsualScore()) && !Double.isNaN(score.getFinalScore())) {
                double sum = percentage1 * score.getExperimentScore() + percentage2 * score.getUsualScore() + percentage3 * score.getFinalScore();
                String str = String.format("%.1f", sum);
                double result = Math.round(Double.parseDouble(str));

                studentInformationMAPPER.UpdateComprehensiveScore(result, score.getId());
            }
        }
    }

    //导出成绩分析PDF
    @Override
    public ResponseEntity<byte[]> exportComprehensiveScoreAnalyse(int courseId) {
        analyse(courseId);
        try {
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

            CellStyle style2 = workbook.createCellStyle();
            style2.setBorderBottom(BorderStyle.THIN);
            style2.setBorderTop(BorderStyle.THIN);
            style2.setBorderRight(BorderStyle.THIN);
            style2.setBorderLeft(BorderStyle.THIN);

            QueryWrapper<CourseScoreAnalyse> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("course_id", courseId);
            CourseScoreAnalyse scoreAnalyse = courseScoreAnalyseMAPPER.selectOne(queryWrapper);

            export.valueToCell(sheet, 1, 1, "分数段", style2);
            export.valueToCell(sheet, 1, 2, "优", style2);
            export.valueToCell(sheet, 2, 2, ">= 90", style2);
            export.valueToCell(sheet, 3, 2, scoreAnalyse.getSuperior().toString(), style2);
            export.valueToCell(sheet, 4, 2, String.valueOf(export.doubleFormat(scoreAnalyse.getSuperior() * 1.0 / scoreAnalyse.getStudentNum(), 4) * 100), style2);

            export.valueToCell(sheet, 1, 3, "良", style2);
            export.valueToCell(sheet, 2, 3, "80-89", style2);
            export.valueToCell(sheet, 3, 3, scoreAnalyse.getGreat().toString(), style2);
            export.valueToCell(sheet, 4, 3, String.valueOf(export.doubleFormat(scoreAnalyse.getGreat() * 1.0 / scoreAnalyse.getStudentNum(), 4) * 100), style2);

            export.valueToCell(sheet, 1, 4, "中", style2);
            export.valueToCell(sheet, 2, 4, "70-79", style2);
            export.valueToCell(sheet, 3, 4, scoreAnalyse.getGood().toString(), style2);
            export.valueToCell(sheet, 4, 4, String.valueOf(export.doubleFormat(scoreAnalyse.getGood() * 1.0 / scoreAnalyse.getStudentNum(), 4) * 100), style2);

            export.valueToCell(sheet, 1, 5, "及格", style2);
            export.valueToCell(sheet, 2, 5, "60-69", style2);
            export.valueToCell(sheet, 3, 5, scoreAnalyse.getPass().toString(), style2);
            export.valueToCell(sheet, 4, 5, String.valueOf(export.doubleFormat(scoreAnalyse.getPass() * 1.0 / scoreAnalyse.getStudentNum(), 4) * 100), style2);

            export.valueToCell(sheet, 1, 6, "不及格", style2);
            export.valueToCell(sheet, 2, 6, "< 60", style2);
            export.valueToCell(sheet, 3, 6, scoreAnalyse.getFailed().toString(), style2);
            export.valueToCell(sheet, 4, 6, String.valueOf(export.doubleFormat(scoreAnalyse.getFailed() * 1.0 / scoreAnalyse.getStudentNum(), 4) * 100), style2);

            export.valueToCell(sheet, 3, 1, "人数", style2);
            export.valueToCell(sheet, 4, 1, "比例", style2);
            export.valueToCell(sheet, 5, 1, "最高分", style2);
            export.valueToCell(sheet, 5, 2, String.valueOf(scoreAnalyse.getMaxScore()), style2);
            export.valueToCell(sheet, 5, 3, "最低分", style2);
            export.valueToCell(sheet, 5, 4, String.valueOf(scoreAnalyse.getMinScore()), style2);
            export.valueToCell(sheet, 5, 5, "平均分", style2);
            export.valueToCell(sheet, 5, 6, String.valueOf(scoreAnalyse.getAverageScore()), style2);
            export.valueToCell(sheet, 6, 1, "及格率", style2);
            export.valueToCell(sheet, 6, 2, String.valueOf(scoreAnalyse.getPassRate()), style2);

            CellRangeAddress mergedRegion = new CellRangeAddress(1, 6, 0, 0);
            sheet.addMergedRegion(mergedRegion);
            export.valueToCell(sheet, 1, 0, "综合成绩", style);
            export.reloadCellStyle(mergedRegion, sheet, style);

            mergedRegion = new CellRangeAddress(0, 0, 0, 6);
            sheet.addMergedRegion(mergedRegion);
            export.valueToCell(sheet, 0, 0, "课程成绩分析", style);
            export.reloadCellStyle(mergedRegion, sheet, style);

            ByteArrayOutputStream outputStreamXLS = new ByteArrayOutputStream();
            workbook.write(outputStreamXLS);

            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStreamXLS.toByteArray());

            // 加载 XLS 文件
            com.spire.xls.Workbook workbookn = new com.spire.xls.Workbook();
            workbookn.loadFromStream(inputStream);

            workbookn.getConverterSetting().setSheetFitToPage(true);
            workbookn.getConverterSetting().setSheetFitToWidth(true);
            // 将 XLS 文件转换为 PDF 文件
            ByteArrayOutputStream outputStreamPDF = new ByteArrayOutputStream();

            workbookn.saveToStream(outputStreamPDF, FileFormat.PDF);
            //使用字节数组读取
            byte[] pdfBytes = outputStreamPDF.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=converted.pdf");
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (IOException e) {
            return null;
        }
    }

    //课程成绩分析
    @Override
    public void analyse(int courseId) {
        CourseScoreAnalyse scoreAnalyse = new CourseScoreAnalyse();
        scoreAnalyse.setCourseId(courseId);
        scoreAnalyse.setStudentNum(studentInformationMAPPER.count(courseId));
        scoreAnalyse.setMaxScore(studentInformationMAPPER.max(courseId));
        scoreAnalyse.setMinScore(studentInformationMAPPER.min(courseId));

        double average = studentInformationMAPPER.average(courseId);
        String str1 = String.format("%.1f", average);
        double result1 = Double.parseDouble(str1);
        scoreAnalyse.setAverageScore(result1);

        scoreAnalyse.setSuperior(studentInformationMAPPER.superior(courseId));
        scoreAnalyse.setGreat(studentInformationMAPPER.great(courseId));
        scoreAnalyse.setGood(studentInformationMAPPER.good(courseId));
        scoreAnalyse.setPass(studentInformationMAPPER.pass(courseId));
        scoreAnalyse.setFailed(studentInformationMAPPER.failed(courseId));

        double sum = studentInformationMAPPER.passNum(courseId) * 1.0 / studentInformationMAPPER.count(courseId);
        String str = String.format("%.4f", sum);
        double result = Double.parseDouble(str);
        scoreAnalyse.setPassRate(result);

        QueryWrapper<CourseScoreAnalyse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", courseId);

        if (courseScoreAnalyseMAPPER.update(scoreAnalyse, queryWrapper) != 1) {
            courseScoreAnalyseMAPPER.insert(scoreAnalyse);
        }
    }
}
