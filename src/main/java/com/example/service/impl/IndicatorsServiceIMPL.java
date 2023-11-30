package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.IndicatorOutlineMAPPER;
import com.example.mapper.IndicatorsMAPPER;
import com.example.object.CourseBasicInformation;
import com.example.object.IndicatorOutline;
import com.example.object.Indicators;
import com.example.object.comprehensiveAnalyse.ExamPaperAnalyseReport;
import com.example.service.IndicatorsSERVICE;
import com.example.utility.DataResponses;
import com.example.utility.export.export;
import com.sini.com.spire.doc.*;
import com.sini.com.spire.doc.Table;
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
    AnalysisReportServiceIMPL analysisReportServiceIMPL;

    @Override
    public ResponseEntity<byte[]> IndicatorsPDF(String major, String version) {
        try {
            //工作簿事例
            int rowIndex = 2;
            int columIndex = 0;

            HSSFWorkbook workbook = new HSSFWorkbook();
            Sheet sheet = workbook.createSheet();

            //单元格样式
            CellStyle style = workbook.createCellStyle();
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setWrapText(true);
            style.setVerticalAlignment(VerticalAlignment.CENTER);

            CellStyle style2 = workbook.createCellStyle();
            style2.setBorderBottom(BorderStyle.THIN);
            style2.setBorderTop(BorderStyle.THIN);
            style2.setBorderRight(BorderStyle.THIN);
            style2.setBorderLeft(BorderStyle.THIN);
            style2.setWrapText(true);
            style2.setAlignment(HorizontalAlignment.CENTER);
            style2.setVerticalAlignment(VerticalAlignment.CENTER);

            Row row1 = sheet.createRow(0);
            row1.setRowStyle(style2);
            CellRangeAddress mergedRegion2 = new CellRangeAddress(0, 0, 0, 2);
            sheet.addMergedRegion(mergedRegion2);
            String pdfTitle = major + "专业指标点";
            if (!Objects.equals(version, "NotSelect")) pdfTitle = version + '级' + pdfTitle;
            row1.createCell(0).setCellValue(pdfTitle);
            export.reloadCellStyle(mergedRegion2, sheet, style2);

            Row row = sheet.createRow(1);
            row.setRowStyle(style2);
            CellRangeAddress mergedRegion = new CellRangeAddress(1, 1, 0, 1);
            sheet.addMergedRegion(mergedRegion);
            row.createCell(0).setCellValue("毕业要求（知识、能力与素质要求）");
            sheet.setColumnWidth(0, 25 * 256);
            sheet.setColumnWidth(1, 30 * 256);
            sheet.setColumnWidth(2, 30 * 256);
            export.reloadCellStyle(mergedRegion, sheet, style2);
            export.valueToCell(sheet, 1, 2, "实现课程（开出课程）", style2);

            QueryWrapper<IndicatorOutline> queryWrapper = new QueryWrapper<>();
            queryWrapper.orderByAsc("id");
            List<IndicatorOutline> indicatorOutlines = indicatorOutlineMAPPER.selectList(queryWrapper);
            int temp = rowIndex;
            for (IndicatorOutline outline : indicatorOutlines) {
                Integer id = outline.getId();
                QueryWrapper<Indicators> queryWrapper1 = new QueryWrapper<>();
                queryWrapper1.eq("indicator_index", id);
                queryWrapper1.like("major", major);
                queryWrapper1.like(!Objects.equals(version, "NotSelect"), "version", version);
                List<Indicators> indicators = indicatorsMAPPER.selectList(queryWrapper1);
                if (indicators.size() == 0) {
                    String S = outline.getName() + "\n" + outline.getContent();
                    export.valueToCell(sheet, temp, 0, S, style);
                    rowIndex++;
                } else {
                    for (Indicators indicator : indicators) {
                        sheet.createRow(rowIndex).setRowStyle(style);
                        String s = indicator.getIndicatorName() + "\n" + indicator.getIndicatorContent();
                        export.valueToCell(sheet, rowIndex, 1, s, style);
                        export.valueToCell(sheet, rowIndex, 2, indicator.getCourses(), style);
                        rowIndex++;
                    }
                    String S = outline.getName() + "\n" + outline.getContent();
                    export.valueToCell(sheet, temp, 0, S, style);
                    mergedRegion = new CellRangeAddress(temp, rowIndex - 1, 0, 0);
                    sheet.addMergedRegion(mergedRegion);
                    export.reloadCellStyle(mergedRegion, sheet, style);
                }
                temp = rowIndex;
            }

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


//            加载数据填充表格
            QueryWrapper<IndicatorOutline> indicatorOutlinesQueryWrapper = new QueryWrapper<>();
            indicatorOutlinesQueryWrapper.orderByAsc("id");
            List<IndicatorOutline> indicatorOutlines = indicatorOutlineMAPPER.selectList(indicatorOutlinesQueryWrapper);

            QueryWrapper<Indicators> indicatorsQueryWrapper = new QueryWrapper<>();
            indicatorsQueryWrapper.eq("major", major);
            indicatorsQueryWrapper.eq(null != version, "version", version);
            List<Indicators> indicators = indicatorsMAPPER.selectList(indicatorsQueryWrapper);

            //初始化表格
            Table table = generateTable(section, indicators.size() + 2, 3);

//            添加表格数据
            String title = major + "专业指标点";
            if (null != version) title = version + "级" + title;
            table.get(0, 0).addParagraph().appendText(title);
            table.applyHorizontalMerge(0, 0, 2);
            TableRow tableTitleRow = table.getRows().get(0);
            TableCell titleCell = tableTitleRow.getCells().get(0);
            titleCell.getParagraphs().get(0).getFormat().setHorizontalAlignment(com.sini.com.spire.doc.documents.HorizontalAlignment.Center);
            table.get(1, 0).addParagraph().appendText("毕业要求（知识、能力与素质要求）");
            table.applyHorizontalMerge(1, 0, 1);
            table.get(1, 2).addParagraph().appendText("实现课程（开出课程）");
            TableRow tableRow1 = table.getRows().get(1);
            TableCell titleCell10 = tableRow1.getCells().get(0);
            TableCell titleCell11 = tableRow1.getCells().get(2);
            titleCell10.getParagraphs().get(0).getFormat().setHorizontalAlignment(com.sini.com.spire.doc.documents.HorizontalAlignment.Center);
            titleCell11.getParagraphs().get(0).getFormat().setHorizontalAlignment(com.sini.com.spire.doc.documents.HorizontalAlignment.Center);

            int row = 2;
            for (IndicatorOutline indicatorOutline : indicatorOutlines) {
                table.get(row, 0).addParagraph().appendText(indicatorOutline.getName() + "\n" + indicatorOutline.getContent());
                int ver = row;
                for (int indicatorNum = 0; indicatorNum < indicators.size(); indicatorNum++) {
                    if (Objects.equals(indicators.get(indicatorNum).getIndicatorIndex(), indicatorOutline.getId())) {
                        table.get(row, 1).addParagraph().appendText(indicators.get(indicatorNum).getIndicatorName() + "\n" + indicators.get(indicatorNum).getIndicatorContent());
                        table.get(row, 2).addParagraph().appendText(indicators.get(indicatorNum).getCourses());
                        row++;
                    }
                }
                table.applyVerticalMerge(0, ver, row-1);
            }

//          设置表格内容垂直居中
            for (int i = 0; i < table.getRows().getCount(); i++) {
                TableRow tableRow = table.getRows().get(i);
                for (int j = 0; j < table.getDefaultColumnsNumber(); j++) {
                    TableCell cell = tableRow.getCells().get(j);
                    cell.getCellFormat().setVerticalAlignment(com.sini.com.spire.doc.documents.VerticalAlignment.Middle);
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
