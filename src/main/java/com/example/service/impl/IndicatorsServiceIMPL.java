package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.IndicatorOutlineMAPPER;
import com.example.mapper.IndicatorsMAPPER;
import com.example.object.IndicatorOutline;
import com.example.object.Indicators;
import com.example.service.IndicatorsSERVICE;
import com.example.utility.DocPOI.ExcelWriter;
import com.example.utility.DocPOI.WordWriter;
import com.example.utility.export.export;
import com.sini.com.spire.doc.Table;
import com.sini.com.spire.doc.*;
import com.sini.com.spire.doc.documents.*;
import com.spire.xls.FileFormat;
import com.spire.xls.Workbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xwpf.usermodel.TableRowAlign;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

@Service
public class IndicatorsServiceIMPL extends ServiceImpl<IndicatorsMAPPER, Indicators> implements IndicatorsSERVICE {

    @Autowired
    private IndicatorsMAPPER indicatorsMAPPER;
    @Autowired
    private IndicatorOutlineMAPPER indicatorOutlineMAPPER;

    @Autowired
    private AnalysisReportServiceIMPL analysisReportServiceIMPL;

    @Override
    public ResponseEntity<byte[]> IndicatorsPDF(String major, String version) {
        try {
            //查询指标点信息
            LambdaQueryWrapper<Indicators> indicatorsWrapper = new LambdaQueryWrapper<>();
            indicatorsWrapper.eq(Indicators::getMajor, major)
                    .eq(Indicators::getVersion, version)
                    .orderByAsc(Indicators::getId);
            List<Indicators> indicatorsList = indicatorsMAPPER.selectList(indicatorsWrapper);
            List<IndicatorOutline> indicatorOutlineList = indicatorOutlineMAPPER.selectList(null);

            //初始化Excel文件对象
            ExcelWriter excelWriter = new ExcelWriter();
            //初始化列宽
            excelWriter.setColWidth(0, 20);
            excelWriter.setColWidth(1, 30);
            excelWriter.setColWidth(2, 25);
            excelWriter.setColWidth(3, 5);
            excelWriter.setColWidth(4, 20);
            excelWriter.setColWidth(5, 5);
            excelWriter.setColWidth(6, 10);
            //设置表头信息
            excelWriter.setValue(0, 0, version + '版' + major + "专业指标点");
            excelWriter.cellMerge(0, 0, 0, 6);
            excelWriter.setValue(1, 0, "毕业要求（知识、能力与素质要求）");
            excelWriter.cellMerge(1, 1, 0, 1);
            excelWriter.setValue(1, 2, "实现课程（开出课程）");
            excelWriter.cellMerge(1, 1, 2, 6);
            excelWriter.setValue(2, 0, "指标点大纲");
            excelWriter.setValue(2, 1, "指标点内容");
            excelWriter.setValue(2, 2, "课程名称");
            excelWriter.setValue(2, 3, "学分");
            excelWriter.setValue(2, 4, "课程性质");
            excelWriter.setValue(2, 5, "权重");
            excelWriter.setValue(2, 6, "权重合计");
            //填充指标点内容
            int startRow = 3, courseNum = 0, indicatorCourseNum = 0;
            double weightTotal = 0;
            String indicatorNameIndex = "1.1";
            excelWriter.setValue(startRow, 1, indicatorsList.get(0).getIndicatorName() + "\n" + indicatorsList.get(0).getIndicatorContent());
            for (int indicatorOutlineIndex = 0;
                 indicatorOutlineIndex < indicatorOutlineList.size();
                 indicatorOutlineIndex++) {
                excelWriter.setValue(startRow, 0,
                        indicatorOutlineList.get(indicatorOutlineIndex).getName() + "\n"
                                + indicatorOutlineList.get(indicatorOutlineIndex).getContent());
                for (int indicatorIndex = 0;
                     indicatorIndex < indicatorsList.size();
                     indicatorIndex++) {
                    if (indicatorsList.get(indicatorIndex).getIndicatorIndex() ==
                            indicatorOutlineList.get(indicatorOutlineIndex).getId()) {
                        if (!indicatorNameIndex.equals(indicatorsList.get(indicatorIndex).getIndicatorName())) {
                            excelWriter.setValue(startRow, 1, indicatorsList.get(indicatorIndex).getIndicatorName() + "\n" + indicatorsList.get(indicatorIndex).getIndicatorContent());
                            excelWriter.cellMerge(startRow - indicatorCourseNum, startRow - 1, 1, 1);
                            excelWriter.cellMerge(startRow - indicatorCourseNum, startRow - 1, 6, 6);
                            excelWriter.setValue(startRow - indicatorCourseNum, 6, String.format("%.2f", weightTotal));
                            indicatorNameIndex = indicatorsList.get(indicatorIndex).getIndicatorName();
                            indicatorCourseNum = 0;
                            weightTotal = 0;
                        }
                        excelWriter.setValue(startRow, 2, indicatorsList.get(indicatorIndex).getCourseName());
                        excelWriter.setValue(startRow, 3, indicatorsList.get(indicatorIndex).getCredit());
                        excelWriter.setValue(startRow, 4, indicatorsList.get(indicatorIndex).getCourseType());
                        excelWriter.setValue(startRow++, 5, indicatorsList.get(indicatorIndex).getWeight());
                        weightTotal += indicatorsList.get(indicatorIndex).getWeight();
                        courseNum++;
                        indicatorCourseNum++;
                    }
                }
                excelWriter.cellMerge(startRow - courseNum, startRow - 1, 0, 0);
                courseNum = 0;
            }
            excelWriter.setValue(startRow - indicatorCourseNum, 6, String.format("%.2f", weightTotal));
            excelWriter.cellMerge(startRow - indicatorCourseNum, startRow - 1, 1, 1);
            excelWriter.cellMerge(startRow - indicatorCourseNum, startRow - 1, 6, 6);

            //设置单元格居中
            for (int rowIndex = 0; rowIndex < 3 + indicatorsList.size(); rowIndex++) {
                for (int colIndex = 0; colIndex < 7; colIndex++) {
                    Cell cell = excelWriter.getCell(rowIndex, colIndex);
                    if (2 < rowIndex && 2 > colIndex) {
                        cell.setCellStyle(excelWriter.VerticalCenter());
                        continue;
                    }
                    cell.setCellStyle(excelWriter.HorizontalVerticalCenter());
                }
            }
            //设置黑色细边框
            excelWriter.setEditAreaCellBlackTHINBorder();

            excelWriter.saveFile("indicatorTemp.xlsx");
            ExcelWriter.saveToPDF("indicatorTemp.xlsx", "indicators.pdf");
            //使用字节数组读取
            byte[] pdfBytes = Files.readAllBytes(Paths.get("indicators.pdf"));

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=converted.pdf");
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (IOException ignored) {
        }
        return null;
    }

    //    生成指标点Word文档
    @Override
    public ResponseEntity<byte[]> IndicatorsWord(HttpServletResponse response, String major, String version) {
        try {
            //查询指标点信息
            List<Indicators> indicatorsList = indicatorsMAPPER.selectList(
                    new LambdaQueryWrapper<Indicators>()
                            .eq(Indicators::getMajor, major)
                            .eq(Indicators::getVersion, version)
                            .orderByAsc(Indicators::getId));
            List<IndicatorOutline> indicatorOutlineList = indicatorOutlineMAPPER.selectList(null);

            //获得Word对象
            WordWriter wordWriter = new WordWriter();
            XWPFTable table = wordWriter.getTable(3 + indicatorsList.size(), 7);
            table.setWidth("125%");
            table.setTableAlignment(TableRowAlign.LEFT);

            //表头信息
            wordWriter.setTableCellValue(table, 0, 0, version + '版' + major + "专业指标点", true, true);
            wordWriter.mergeCells(table, 0, 0, 0, 6);
            wordWriter.setTableCellValue(table, 1, 0, "毕业要求（知识、能力与素质要求）", true, true);
            wordWriter.mergeCells(table, 1, 1, 0, 1);
            wordWriter.setTableCellValue(table, 1, 2, "实现课程（开出课程）", true, true);
            wordWriter.mergeCells(table, 1, 1, 2, 6);
            wordWriter.setTableCellValue(table, 2, 0, "指标点大纲", true, true);
            wordWriter.setTableCellValue(table, 2, 1, "指标点内容", true, true);
            wordWriter.setTableCellValue(table, 2, 2, "课程名称", true, true);
            wordWriter.setTableCellValue(table, 2, 3, "学分", true, true);
            wordWriter.setTableCellValue(table, 2, 4, "课程性质", true, true);
            wordWriter.setTableCellValue(table, 2, 5, "权重", true, true);
            wordWriter.setTableCellValue(table, 2, 6, "权重合计", true, true);

            //填充数据内容
            int startRow = 3, courseNum = 0, indicatorCourse = 0;
            double weightTotal = 0;
            String indicatorNameIndex = "1.1";
            wordWriter.setTableCellValue(table, startRow, 1, indicatorsList.get(0).getIndicatorName(), false, true);
            table.getRow(startRow).getCell(1)
                    .addParagraph().createRun().setText(indicatorsList.get(0).getIndicatorContent());
            for (IndicatorOutline indicatorOutline : indicatorOutlineList) {
                wordWriter.setTableCellValue(table, startRow, 0, indicatorOutline.getName(), false, true);
                table.getRow(startRow).getCell(0)
                        .addParagraph().createRun().setText(indicatorOutline.getContent());
                for (Indicators indicator : indicatorsList) {
                    if (indicator.getIndicatorIndex().equals(indicatorOutline.getId())) {
                        if (!indicatorNameIndex.equals(indicator.getIndicatorName())) {
                            indicatorNameIndex = indicator.getIndicatorName();
                            wordWriter.mergeCellsVertically(table, 1, startRow - indicatorCourse, startRow - 1);
                            wordWriter.setTableCellValue(table, startRow - indicatorCourse, 6, String.format("%.2f", weightTotal), true, true);
                            wordWriter.mergeCellsVertically(table, 6, startRow - indicatorCourse, startRow - 1);
                            wordWriter.setTableCellValue(table, startRow, 1, indicator.getIndicatorName(), false, true);
                            table.getRow(startRow).getCell(1)
                                    .addParagraph().createRun().setText(indicatorsList.get(0).getIndicatorContent());
                            weightTotal = 0;
                            indicatorCourse = 0;
                        }
                        wordWriter.setTableCellValue(table, startRow, 2, indicator.getCourseName(), true, true);
                        wordWriter.setTableCellValue(table, startRow, 3, indicator.getCredit().toString(), true, true);
                        wordWriter.setTableCellValue(table, startRow, 4, indicator.getCourseType(), true, true);
                        wordWriter.setTableCellValue(table, startRow++, 5, indicator.getWeight().toString(), true, true);
                        courseNum++;
                        indicatorCourse++;
                        weightTotal += indicator.getWeight();
                    }
                }
                wordWriter.mergeCellsVertically(table, 0, startRow - courseNum, startRow - 1);
                courseNum = 0;
            }
            wordWriter.mergeCellsVertically(table, 1, startRow - indicatorCourse, startRow - 1);
            wordWriter.setTableCellValue(table, startRow - indicatorCourse, 6, String.format("%.2f", weightTotal), true, true);
            wordWriter.mergeCellsVertically(table, 6, startRow - indicatorCourse, startRow - 1);

            //设置列宽
            wordWriter.setTableColWidth(table, 0, 0.20);
            wordWriter.setTableColWidth(table, 1, 0.22);
            wordWriter.setTableColWidth(table, 2, 0.18);
            wordWriter.setTableColWidth(table, 3, 0.06);
            wordWriter.setTableColWidth(table, 4, 0.18);
            wordWriter.setTableColWidth(table, 5, 0.06);
            wordWriter.setTableColWidth(table, 6, 0.10);

            String fileName = "indicators.docx";
            wordWriter.saveToFile(fileName);
            byte[] Bytes = Files.readAllBytes(Paths.get(fileName));

            response.reset();
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/msword");
            response.setHeader("Content-Disposition", "attachment;fileName=" + new String(fileName.getBytes(), StandardCharsets.ISO_8859_1));

            return ResponseEntity.ok().body(Bytes);
        } catch (IOException ignored) {
            return null;
        }
    }

}
