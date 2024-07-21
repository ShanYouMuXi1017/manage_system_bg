package com.example.utility.DocPOI;

import com.sini.com.spire.doc.Document;
import com.sini.com.spire.doc.FileFormat;
import com.spire.pdf.PdfConvertOptions;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 使用XWPFDocument, 写入word文档(.docx格式)
 * <p> 对于XWPFParagraph paragraph = document.createParagraph();</p>
 * <p> 简单说明：对于一个word文档, 有: 段落(一个回车算一个段落,包括任意标题), 表格, 图片, 页眉页脚...组成</p>
 * <a href="https://deepoove.com/poi-tl/apache-poi-guide.html">Apache POI Word文档参考阅读</a>
 */
public class WordWriter {
    public XWPFDocument document;

    /**
     * 构造word文档对象
     */
    public WordWriter() {
        this.document = new XWPFDocument();
    }

    /**
     * 获得一个段落
     *
     * <p>XWPFRun textRun = paragraph.createRun();</p>
     * <p>段落是一个基本单位, 而 文本运行(XWPFRun) 是段落的基本组成单元, 它可以是文本/图片</p>
     * <p>获得XWPFRun之后即可调用set方法对 文字进行样式的设置</p>
     *
     * @return 段落对象
     */
    public XWPFParagraph getParagraph() {
        return this.document.createParagraph();
    }

    /**
     * 获得一个Word表格
     * <p>默认一行一列</p>
     *
     * @return 表格对象
     */
    public XWPFTable getTable() {
        return getTable(1, 1);
    }

    /**
     * 获得一个Word表格
     *
     * @param rowNum 表格行数
     * @param colNum 表格列数
     * @return 表格对象
     */
    public XWPFTable getTable(int rowNum, int colNum) {
        XWPFTable table = this.document.createTable(rowNum, colNum);
        table.setWidth("100%");
        for (int rowIndex = 0; rowIndex < rowNum; rowIndex++) {
            for (int colIndex = 0; colIndex < colNum; colIndex++) {
                table.getRow(rowIndex).getCell(colIndex).setText("");
            }
        }
        return table;
    }


    /**
     * 为文档中第一个表格设置某个单元格的内容
     * <p>默认单元格没有水平居中对齐</p>
     *
     * @param rowIndex 指定的单元格行
     * @param colIndex 指定的单元格列
     * @param value    需要填充的内容
     * @return 返回单元格对象, 以便设计具体样式
     */
    public XWPFTableCell setTableCellValue(int rowIndex, int colIndex, String value) {
        return setTableCellValue(this.document.getTables().get(0), rowIndex, colIndex, value);
    }

    /**
     * 为指定的表格设置某个单元格的内容
     * <p>默认单元格没有水平居中对齐</p>
     *
     * @param table    指定的word表格
     * @param rowIndex 指定的单元格行
     * @param colIndex 指定的单元格列
     * @param value    需要填充的内容
     * @return 返回单元格对象, 以便设计具体样式
     */
    public XWPFTableCell setTableCellValue(XWPFTable table, int rowIndex, int colIndex, String value) {
        return setTableCellValue(table, rowIndex, colIndex, value, false, false);
    }

    /**
     * 为指定的表格设置某个单元格的内容, 支持水平和垂直居中单元格内容
     *
     * @param table            指定的word表格
     * @param rowIndex         指定的单元格行
     * @param colIndex         指定的单元格列
     *                         内 @param value            需要填充的字符容
     * @param horizontalCenter 是否水平垂直
     * @param verticalCenter   是否居中垂直
     * @return 返回单元格对象, 以便设计具体样式
     */
    public XWPFTableCell setTableCellValue(
            XWPFTable table,
            int rowIndex,
            int colIndex,
            String value,
            boolean horizontalCenter,
            boolean verticalCenter) {
        XWPFTableCell tableCell = table.getRow(rowIndex).getCell(colIndex);
        tableCell.setText(value);

        //垂直居中
        if (verticalCenter) {
            tableCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        }
        //水平居中
        if (horizontalCenter) {
            tableCell.getCTTc().getPList().get(0).addNewPPr().addNewJc().setVal(STJc.CENTER);
        }
        return tableCell;
    }


    /**
     * 设置word表格某一列的宽度
     *
     * @param table        指定的表格
     * @param colIndex     指定的列
     * @param widthPercent 宽度占比(0 ~ 1 之间的两位小数)
     */
    public void setTableColWidth(XWPFTable table, int colIndex, double widthPercent) {
        try {
            if (!(widthPercent <= 1 || widthPercent >= 0)) {
                throw new Exception("widthPercent需要0~1之间的两位小数");
            }
            widthPercent *= 100;
            widthPercent = (int) widthPercent;
            for (int rowIndex = 0; rowIndex < table.getRows().size(); rowIndex++) {
                table.getRow(rowIndex).getCell(colIndex).setWidth(String.valueOf(widthPercent) + '%');
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 合并单元格
     *
     * @param table    需要合并的表格
     * @param startRow 开始行
     * @param endRow   结束行
     * @param startCol 开始列
     * @param endCol   结束列
     * @return 执行状态
     */
    public boolean mergeCells(XWPFTable table, int startRow, int endRow, int startCol, int endCol) {
        boolean status = true;
        try {
            if (startRow > endRow || startCol > endCol) {
                throw new Exception("需要: startRow <= endRow || startCol <= endCol");
            }
            for (int rowIndex = startRow; rowIndex <= endRow; rowIndex++) {
                mergeCellsHorizontal(table, rowIndex, startCol, endCol);
            }
            for (int colIndex = startCol; colIndex <= endCol; colIndex++) {
                mergeCellsVertically(table, colIndex, startRow, endRow);
            }
        } catch (Exception e) {
            e.printStackTrace();
            status = false;
        }
        return status;
    }

    /**
     * 合并某一行的 指定单元格(只保留合并后的第一个单元格内容)
     *
     * @param table     表格
     * @param targetRow 合并目标行
     * @param startCell 开始单元格(列)
     * @param endCell   结束单元格(列)
     * @return 合并结果
     */
    public boolean mergeCellsHorizontal(XWPFTable table, int targetRow, int startCell, int endCell) {
        boolean status = true;
        try {
            for (int i = startCell; i <= endCell; i++) {
                XWPFTableCell cell = table.getRow(targetRow).getCell(i);
                if (i == startCell) {
                    cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);
                } else {
                    cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            status = false;
        }
        return status;
    }

    /**
     * 合并某一列的 指定单元格(只保留合并后的第一个单元格内容)
     *
     * @param table     表格
     * @param targetCol 合并行所在列
     * @param startCell 开始行
     * @param endCell   结束行
     * @return 合并结果
     */
    public boolean mergeCellsVertically(XWPFTable table, int targetCol, int startCell, int endCell) {
        boolean status = true;
        try {
            for (int i = startCell; i <= endCell; i++) {
                XWPFTableCell cell = table.getRow(i).getCell(targetCol);
                if (i == startCell) {
                    cell.getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.RESTART);
                } else {
                    cell.getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.CONTINUE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            status = false;
        }
        return status;
    }

    /**
     * 保存为Word文件
     *
     * @param wordPath 文档名称(可带路径)
     * @return 保存状态
     */
    public boolean saveToFile(String wordPath) {
        boolean status = true;
        try {
            if (!wordPath.endsWith(".docx")) {
                throw new Exception("Word文件名称错误(需要.docx后缀)");
            }
            //FileOutputStream wordFileOutputStream = new FileOutputStream(this.fileName);
            this.document.write(Files.newOutputStream(Paths.get(wordPath)));
            //wordFileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            status = false;
        }
        return status;
    }

    /**
     * 基于spire的简单封装, 实现将Word转为pdf文档
     *
     * @param wordPath word文档路径(文件名)
     * @param PDFPath  要导出的pdf文件路径(文件名)
     * @return 执行状态
     */
    public static boolean saveToPDF(String wordPath, String PDFPath) {
        boolean status = true;
        try {
            if ((wordPath.endsWith(".docx") || wordPath.endsWith(".doc") && PDFPath.endsWith(".pdf"))) {
                Document doc = new Document(wordPath);
                doc.saveToFile(PDFPath, FileFormat.PDF);
                doc.close();
            } else {
                throw new Exception("文件后缀不正确! (需要 .docx | .doc | .pdf");
            }
        } catch (Exception e) {
            e.printStackTrace();
            status = false;
        }
        return status;
    }

}
