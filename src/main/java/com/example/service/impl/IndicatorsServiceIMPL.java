package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.IndicatorOutlineMAPPER;
import com.example.mapper.IndicatorsMAPPER;
import com.example.object.IndicatorOutline;
import com.example.object.Indicators;
import com.example.service.IndicatorsSERVICE;
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
            //工作簿事例
            HSSFWorkbook workbook = new HSSFWorkbook();
            Sheet sheet = workbook.createSheet();

            //单元格样式
//            仅垂直居中
            CellStyle style = workbook.createCellStyle();
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setWrapText(true);
            style.setVerticalAlignment(VerticalAlignment.CENTER);

//            水平垂直居中
            CellStyle style2 = workbook.createCellStyle();
            style2.setBorderBottom(BorderStyle.THIN);
            style2.setBorderTop(BorderStyle.THIN);
            style2.setBorderRight(BorderStyle.THIN);
            style2.setBorderLeft(BorderStyle.THIN);
            style2.setWrapText(true);
            style2.setAlignment(HorizontalAlignment.CENTER);
            style2.setVerticalAlignment(VerticalAlignment.CENTER);

//            设置表列宽度
            sheet.setColumnWidth(0, 20 * 256);
            sheet.setColumnWidth(1, 30 * 256);
            sheet.setColumnWidth(2, 25 * 256);
            sheet.setColumnWidth(3, 5 * 256);
            sheet.setColumnWidth(4, 20 * 256);
            sheet.setColumnWidth(5, 5 * 256);
            sheet.setColumnWidth(6, 10 * 256);

//            设置表格标题
            Row rowTitle = sheet.createRow(0);
            CellRangeAddress mergedRegion0006 = new CellRangeAddress(0, 0, 0, 6);
            sheet.addMergedRegion(mergedRegion0006);
            String pdfTitle = version + '版' + major + "专业指标点";
            rowTitle.createCell(0).setCellValue(pdfTitle);
            export.reloadCellStyle(mergedRegion0006, sheet, style2);

//            设置表格字段
            export.valueToCell(sheet, 1, 0, "毕业要求（知识、能力与素质要求）", style2);
            export.valueToCell(sheet, 1, 2, "实现课程（开出课程）", style2);
            CellRangeAddress mergedRegion1101 = new CellRangeAddress(1, 1, 0, 1);
            CellRangeAddress mergedRegion1126 = new CellRangeAddress(1, 1, 2, 6);
            sheet.addMergedRegion(mergedRegion1101);
            sheet.addMergedRegion(mergedRegion1126);

            export.valueToCell(sheet, 2, 0, "指标点大纲", style2);
            export.valueToCell(sheet, 2, 1, "指标点内容", style2);
            export.valueToCell(sheet, 2, 2, "课程名称", style2);
            export.valueToCell(sheet, 2, 3, "学分", style2);
            export.valueToCell(sheet, 2, 4, "课程性质", style2);
            export.valueToCell(sheet, 2, 5, "权重", style2);
            export.valueToCell(sheet, 2, 6, "权重合计", style2);

            LambdaQueryWrapper<Indicators> indicatorsWrapper = new LambdaQueryWrapper<>();
            indicatorsWrapper.eq(Indicators::getMajor, major)
                    .eq(Indicators::getVersion, version)
                    .orderByAsc(Indicators::getId)
//                  标记字段不超过200!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    .last("limit 194");
            List<Indicators> indicatorsList = indicatorsMAPPER.selectList(indicatorsWrapper);
//            List<IndicatorOutline> indicatorOutlineList = indicatorOutlineMAPPER.selectList(null);

            QueryWrapper<IndicatorOutline> test = new QueryWrapper<>();
            test.last("limit 10");
            List<IndicatorOutline> indicatorOutlineList = indicatorOutlineMAPPER.selectList(test);

//            填充指标点内容
            int rowStartIndex = 3, colStartIndex, coursesNum = 3, indicatorNum = 3;
            double totalWeight = 0;
            String indicatorName = indicatorsList.get(0).getIndicatorName();
            for (IndicatorOutline indicatorOutline : indicatorOutlineList) {
                for (int i = 0; i < indicatorsList.size(); i++) {
                    colStartIndex = 2;
                    if (Objects.equals(indicatorOutline.getId(), indicatorsList.get(i).getIndicatorIndex())) {
                        if (!Objects.equals(indicatorName, indicatorsList.get(i).getIndicatorName())) {
                            indicatorName = indicatorsList.get(i).getIndicatorName();
                            CellRangeAddress mergedRegionContent = new CellRangeAddress(coursesNum, rowStartIndex - 1, 1, 1);
                            CellRangeAddress mergedRegionWeight = new CellRangeAddress(coursesNum, rowStartIndex - 1, 6, 6);
                            export.valueToCell(sheet, coursesNum, 1,
                                    indicatorsList.get(i - 1).getIndicatorName() + "\n" + indicatorsList.get(i - 1).getIndicatorContent(),
                                    style);
//                            sheet.getRow(coursesNum).
                            export.valueToCell(sheet, coursesNum, 6,
                                    String.format("%.2f", totalWeight),
                                    style2);
                            sheet.addMergedRegion(mergedRegionContent);
                            sheet.addMergedRegion(mergedRegionWeight);
                            coursesNum = rowStartIndex;
                            totalWeight = 0;
                        }
                        export.valueToCell(sheet, rowStartIndex, colStartIndex++,
                                indicatorsList.get(i).getCourseName(), style2);
                        export.valueToCell(sheet, rowStartIndex, colStartIndex++,
                                indicatorsList.get(i).getCredit().toString(), style2);
                        export.valueToCell(sheet, rowStartIndex, colStartIndex++,
                                indicatorsList.get(i).getCourseType(), style2);
                        export.valueToCell(sheet, rowStartIndex, colStartIndex,
                                indicatorsList.get(i).getWeight().toString(), style2);
                        rowStartIndex++;
                        totalWeight += indicatorsList.get(i).getWeight();
                    }
                }
                CellRangeAddress mergedRegionOutline = new CellRangeAddress(indicatorNum, rowStartIndex - 1, 0, 0);
                export.valueToCell(sheet, indicatorNum, 0,
                        indicatorOutline.getName() + "\n" + indicatorOutline.getContent(),
                        style);
                indicatorNum = rowStartIndex;
                sheet.addMergedRegion(mergedRegionOutline);
            }
            Indicators indicatorLast = indicatorsList.get(indicatorsList.size() - 1);
            CellRangeAddress mergedRegionContent = new CellRangeAddress(coursesNum, rowStartIndex - 1, 1, 1);
            CellRangeAddress mergedRegionWeight = new CellRangeAddress(coursesNum, rowStartIndex - 1, 6, 6);
            export.valueToCell(sheet, coursesNum, 1,
                    indicatorLast.getIndicatorName() + "\n" + indicatorLast.getIndicatorContent(),
                    style);
            export.valueToCell(sheet, coursesNum, 6,
                    String.format("%.2f", totalWeight),
                    style2);
            sheet.addMergedRegion(mergedRegionContent);
            sheet.addMergedRegion(mergedRegionWeight);

            //写入xls文件
            FileOutputStream fileOut = new FileOutputStream("workbook.xls");
            workbook.write(fileOut);
            fileOut.close();

            // 加载 XLS 文件
            Workbook workbookn = new Workbook();
            workbookn.loadFromFile("workbook.xls");
            workbookn.getConverterSetting().setSheetFitToPage(true);
            workbookn.getConverterSetting().setSheetFitToWidth(true);
            // 将 XLS 文件转换为 PDF 文件
            workbookn.saveToFile("workbook.pdf", FileFormat.PDF);
            //使用字节数组读取
            byte[] pdfBytes = Files.readAllBytes(Paths.get("workbook.pdf"));

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
            Document document = new Document();

            //设置基础页面显示效果
            Section section = document.addSection();
            //设置页面大小为A3
            section.getPageSetup().setPageSize(PageSize.A3);
            //设置页面方向为Landscape纵向
            section.getPageSetup().setOrientation(PageOrientation.Portrait);
            //设置页边距
            section.getPageSetup().getMargins().setTop(60f);
            section.getPageSetup().getMargins().setBottom(60f);
            section.getPageSetup().getMargins().setLeft(60f);
            section.getPageSetup().getMargins().setRight(80f);
            //一级标题样式
            ParagraphStyle style1 = new ParagraphStyle(document);
            style1.setName("titleStyle");
            style1.getCharacterFormat().setBold(true);
            //设置字体颜色
            style1.getCharacterFormat().setFontName("宋体");
            style1.getCharacterFormat().setFontSize(30f);
            document.getStyles().add(style1);
            //文本样式
            ParagraphStyle style2 = new ParagraphStyle(document);
            style2.setName("contentStyle");
            style2.getCharacterFormat().setFontName("宋体");
            style2.getCharacterFormat().setFontSize(15f);
            document.getStyles().add(style2);

            //二级标题样式
            ParagraphStyle style3 = new ParagraphStyle(document);
            style3.setName("title2Style");
            style3.getCharacterFormat().setBold(true);
            style3.getCharacterFormat().setFontName("宋体");
            style3.getCharacterFormat().setFontSize(20f);
            document.getStyles().add(style3);


//            加载数据填充表格(spirej限制:至多5个sheet/200行sheet)
            LambdaQueryWrapper<Indicators> indicatorsWrapper = new LambdaQueryWrapper<>();
            indicatorsWrapper.eq(Indicators::getMajor, major)
                    .eq(Indicators::getVersion, version)
                    .orderByAsc(Indicators::getId)
//                  标记字段不超过200!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    .last("limit 194");
            List<Indicators> indicatorsList = indicatorsMAPPER.selectList(indicatorsWrapper);
//            List<IndicatorOutline> indicatorOutlineList = indicatorOutlineMAPPER.selectList(null);

            QueryWrapper<IndicatorOutline> test = new QueryWrapper<>();
            test.last("limit 10");
            List<IndicatorOutline> indicatorOutlineList = indicatorOutlineMAPPER.selectList(test);

            //初始化表格
            Table table = generateTable(section, indicatorsList.size() + 3, 7);

//            添加表格标题
            String title = version + "版" + major + "专业指标点";
            table.get(0, 0).addParagraph().appendText(title);
            table.applyHorizontalMerge(0, 0, 6);
            TableRow tableTitleRow = table.getRows().get(0);
            TableCell titleCell = tableTitleRow.getCells().get(0);
            titleCell.getParagraphs().get(0).getFormat().setHorizontalAlignment(com.sini.com.spire.doc.documents.HorizontalAlignment.Center);

//            设置表格字段
            table.get(1, 0).addParagraph().appendText("毕业要求（知识、能力与素质要求）");
            table.applyHorizontalMerge(1, 0, 1);
            table.get(1, 2).addParagraph().appendText("实现课程（开出课程）");
            table.applyHorizontalMerge(1, 2, 6);
            TableRow tableRow1 = table.getRows().get(1);
            TableCell titleCell10 = tableRow1.getCells().get(0);
            TableCell titleCell12 = tableRow1.getCells().get(2);
            titleCell10.getParagraphs().get(0).getFormat().setHorizontalAlignment(com.sini.com.spire.doc.documents.HorizontalAlignment.Center);
            titleCell12.getParagraphs().get(0).getFormat().setHorizontalAlignment(com.sini.com.spire.doc.documents.HorizontalAlignment.Center);

            table.get(2, 0).addParagraph().appendText("指标点大纲");
            table.get(2, 1).addParagraph().appendText("指标点内容");
            table.get(2, 2).addParagraph().appendText("课程名称");
            table.get(2, 3).addParagraph().appendText("学分");
            table.get(2, 4).addParagraph().appendText("课程性质");
            table.get(2, 5).addParagraph().appendText("权重");
            table.get(2, 6).addParagraph().appendText("权重合计");
            TableRow tableRowField = table.getRows().get(2);
            TableCell FieldCell;
            for (int i = 0; i < table.getDefaultColumnsNumber(); i++) {
                FieldCell = tableRowField.getCells().get(i);
                FieldCell.getParagraphs().get(0).getFormat().setHorizontalAlignment(com.sini.com.spire.doc.documents.HorizontalAlignment.Center);
            }


//            =================================

            int rowStartIndex = 3, colStartIndex, coursesNum = 3, indicatorNum = 3;
            double totalWeight = 0;
            String indicatorName = indicatorsList.get(0).getIndicatorName();
            for (IndicatorOutline indicatorOutline : indicatorOutlineList) {
                for (int i = 0; i < indicatorsList.size(); i++) {
                    colStartIndex = 2;
                    if (Objects.equals(indicatorsList.get(i).getIndicatorIndex(), indicatorOutline.getId())) {
                        if (!Objects.equals(indicatorName, indicatorsList.get(i).getIndicatorName())) {
                            indicatorName = indicatorsList.get(i).getIndicatorName();
                            table.get(coursesNum, 1).addParagraph().appendText(indicatorsList.get(i - 1).
                                    getIndicatorName() + "\n" + indicatorsList.get(i - 1).getIndicatorContent()
                            );
                            table.get(coursesNum, 6).addParagraph().appendText(String.format("%.2f", totalWeight));
                            table.applyVerticalMerge(1, coursesNum, rowStartIndex - 1);
                            table.applyVerticalMerge(6, coursesNum, rowStartIndex - 1);
                            coursesNum = rowStartIndex;
                            totalWeight = 0;

                        }

                        table.get(rowStartIndex, colStartIndex++).addParagraph().appendText(indicatorsList.get(i).getCourseName());
                        table.get(rowStartIndex, colStartIndex++).addParagraph().appendText(String.valueOf(indicatorsList.get(i).getCredit()));
                        table.get(rowStartIndex, colStartIndex++).addParagraph().appendText(indicatorsList.get(i).getCourseType());
                        table.get(rowStartIndex, colStartIndex).addParagraph().appendText(String.valueOf(indicatorsList.get(i).getWeight()));
                        totalWeight += indicatorsList.get(i).getWeight();
                        rowStartIndex++;

                    }

                }

                table.get(indicatorNum, 0).addParagraph().appendText(indicatorOutline.getName() + "\n" + indicatorOutline.getContent());
                table.applyVerticalMerge(0, indicatorNum, rowStartIndex - 1);
                indicatorNum = rowStartIndex;
            }

            Indicators indicatorLast = indicatorsList.get(indicatorsList.size() - 1);
            table.get(coursesNum, 1).addParagraph().appendText(indicatorLast.getIndicatorName() + "\n" + indicatorLast.getIndicatorContent());
            table.get(coursesNum, 6).addParagraph().appendText(String.format("%.2f", totalWeight));
            table.applyVerticalMerge(1, coursesNum, rowStartIndex - 1);
            table.applyVerticalMerge(6, coursesNum, rowStartIndex - 1);

//          设置表格内容垂直居中 和 课程数据水平垂直居中
            for (int i = 0; i < table.getRows().getCount(); i++) {
                TableRow tableRow = table.getRows().get(i);
                for (int j = 0; j < table.getDefaultColumnsNumber(); j++) {
                    TableCell cell = tableRow.getCells().get(j);
                    cell.getCellFormat().setVerticalAlignment(com.sini.com.spire.doc.documents.VerticalAlignment.Middle);
                    if (i > 2 && (j > 1 & j < 6)) {
                        cell.getParagraphs().get(0).getFormat().setHorizontalAlignment(com.sini.com.spire.doc.documents.HorizontalAlignment.Center);
                    }
                }
            }

            byte[] Bytes;
            String fileName = "indicators.docx";
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            response.reset();

            document.saveToStream(outputStream, com.sini.com.spire.doc.FileFormat.Docx);

            Bytes = outputStream.toByteArray();
            outputStream.close();

            response.setCharacterEncoding("utf-8");
            response.setContentType("application/msword");
            response.setHeader("Content-Disposition", "attachment;fileName=" + new String(fileName.getBytes(), StandardCharsets.ISO_8859_1));

            return ResponseEntity.ok().body(Bytes);
        } catch (IOException ignored) {
            return null;
        }
    }

    private Table generateTable(Section section, int row, int col) {
        section.addParagraph();
        Table table = section.addTable(true);
        table.resetCells(row, col);
        //将表格居中对齐（或设置为靠左或靠右）
        table.getTableFormat().setHorizontalAlignment(RowAlignment.Center);
        //设置表格是否跨页断行
        table.getTableFormat().isBreakAcrossPages(false);
        //表格自适应页面宽度
        table.setDefaultColumnWidth(200f);
        table.autoFit(AutoFitBehaviorType.Auto_Fit_To_Window);

        table.setDefaultRowHeight(200f);

        //设置表格的右边框
        table.getTableFormat().getBorders().getRight().setBorderType(com.sini.com.spire.doc.documents.BorderStyle.Hairline);
        table.getTableFormat().getBorders().getRight().setLineWidth(1.0F);
        table.getTableFormat().getBorders().getRight().setColor(java.awt.Color.BLACK);

        //设置表格的顶部边框
        table.getTableFormat().getBorders().getTop().setBorderType(com.sini.com.spire.doc.documents.BorderStyle.Hairline);
        table.getTableFormat().getBorders().getTop().setLineWidth(1.0F);
        table.getTableFormat().getBorders().getTop().setColor(java.awt.Color.BLACK);

        //设置表格的左边框
        table.getTableFormat().getBorders().getLeft().setBorderType(com.sini.com.spire.doc.documents.BorderStyle.Hairline);
        table.getTableFormat().getBorders().getLeft().setLineWidth(1.0F);
        table.getTableFormat().getBorders().getLeft().setColor(java.awt.Color.BLACK);

        //设置表格的底部边框
        table.getTableFormat().getBorders().getBottom().setBorderType(com.sini.com.spire.doc.documents.BorderStyle.Hairline);
        table.getTableFormat().getBorders().getBottom().setLineWidth(1.0F);
        table.getTableFormat().getBorders().getBottom().setColor(Color.BLACK);

        section.addParagraph();
        return table;
    }

}
