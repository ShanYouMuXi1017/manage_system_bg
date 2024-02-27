package com.example.utility.DocPOI;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * 读取Excel文档，支持xls和xlsx，如果需要进行更多关于表的操作，可调用对象的sheet进行
 */
public class ExcelReader {
    public Workbook excelDoc;
    public HSSFWorkbook xlsReader;
    public XSSFWorkbook xlsxReader;
    //默认表
    public Sheet defaultSheet;

    public int num = 1000;

    /**
     * 根据输入流确定表(表号默认为0, 即第一张表)
     *
     * @param excelFis xls或xlsx格式的输入流
     */
    public ExcelReader(FileInputStream excelFis) {
        //try {
        //    this.xlsxReader = new XSSFWorkbook(excelFis);
        //    this.excelDoc = xlsxReader;
        //} catch (Exception xlsxE) {
        //    try {
        //        this.xlsReader = new HSSFWorkbook(excelFis);
        //        this.excelDoc = this.xlsReader;
        //    } catch (Exception xlsE) {
        //        xlsxE.printStackTrace();
        //        xlsE.printStackTrace();
        //    }
        //}
        try {
            this.excelDoc = WorkbookFactory.create(excelFis);
            if (null == this.excelDoc) {
                throw new Exception("Excel文件流读取失败!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        this.defaultSheet = this.excelDoc.getSheetAt(0);
    }

    /**
     * 根据输入流和表号确定默认表
     *
     * @param excelFis   xls或xlsx格式的输入流
     * @param sheetIndex 表号(Excel文件中含有多个表, 可指定)
     */
    public ExcelReader(FileInputStream excelFis, int sheetIndex) {
        try {
            this.excelDoc = WorkbookFactory.create(excelFis);
            if (null == this.excelDoc) {
                throw new Exception("Excel文件流读取失败!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        this.defaultSheet = this.excelDoc.getSheetAt(sheetIndex);
    }

    /**
     * 根据输入流和表号确定默认表
     *
     * @param excelFis  xls或xlsx格式的输入流
     * @param sheetName 表名(Excel文件中含有多个表, 可指定)
     */
    public ExcelReader(FileInputStream excelFis, String sheetName) {
        try {
            this.excelDoc = WorkbookFactory.create(excelFis);
            if (null == this.excelDoc) {
                throw new Exception("Excel文件流读取失败!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        this.defaultSheet = this.excelDoc.getSheet(sheetName);
    }

    public Sheet getSheet(int sheetIndex) {
        return this.excelDoc.getSheetAt(sheetIndex);
    }

    public Sheet getSheet(String sheetName) {
        return this.excelDoc.getSheet(sheetName);
    }

    /**
     * 根据行号和列号获取默认表的单元格的内容
     * 若单元格不含有任何数据，也未被定义将会报空指针异常
     *
     * @param rowIndex 单元格行号
     * @param colIndex 单元格列号
     * @return 此单元格存储的数据
     */
    public Object getValue(int rowIndex, int colIndex) {
        return getValue(this.defaultSheet, rowIndex, colIndex);
    }

    /**
     * 根据行号和列号获取单元格的内容
     * 若单元格不含有任何数据，也未被定义将会报空指针异常
     *
     * @param sheet    指定的表
     * @param rowIndex 单元格行号
     * @param colIndex 单元格列号
     * @return 此单元格存储的数据
     */
    public Object getValue(Sheet sheet, int rowIndex, int colIndex) {
        Cell cell = null;
        Row row = sheet.getRow(rowIndex);
        try {
            cell = sheet.getRow(rowIndex).getCell(colIndex);
        } catch (Exception e) {
            System.out.println("row or cell does not exist! (rowIndex = " + rowIndex + "colIndex = " + colIndex + ")");
            //throw new Exception("row or cell does not exist! (rowIndex = " + rowIndex + "colIndex = " + colIndex + ")");
            //e.printStackTrace();
        }
        Object value = null;
        if (null != cell) {
            switch (cell.getCellType()) {
                //    表示数据未解析
                case _NONE:
                    break;
                //    数值类型的数据，如数字或日期
                case NUMERIC:
                    if (HSSFDateUtil.isCellDateFormatted(cell)) {
                        //日期
                        value = cell.getDateCellValue();
                    } else {
                        //数字
                        value = cell.getNumericCellValue();
                    }
                    break;
                //    字符串类型的数据
                case STRING:
                    value = cell.getStringCellValue();
                    break;
                //    表示单元格包含公式
                case FORMULA:
                    System.out.println("formula");
                    break;
                //    表示单元格为空白，不包含任何数据
                case BLANK:
                    break;
                //    表示单元格包含布尔类型的数据
                case BOOLEAN:
                    value = cell.getBooleanCellValue();
                    break;
                //    表示单元格包含错误值
                case ERROR:
                    value = "ERROR!";
                    break;
                default:
                    System.out.println("数据异常");
            }
        }

        return value;
    }

    /**
     * 获取默认表的行数|含有内容的行数
     *
     * @return 有内容的行数
     */
    public int getNumberOfRows() {
        return getNumberOfRows(this.defaultSheet);
    }

    /**
     * 获取指定表的行数|含有内容的行数
     *
     * @param sheet 指定的表
     * @return 有内容的行数
     */
    public int getNumberOfRows(Sheet sheet) {
        return sheet.getPhysicalNumberOfRows();
    }

    /**
     * 根据行号获取默认表该行的单元格数
     *
     * @param rowIndex 行号
     * @return 此行含有内容的单元格数
     */
    public int getNumberOfCells(int rowIndex) {
        return getNumberOfCells(this.defaultSheet, rowIndex);
    }

    /**
     * 根据行号获取指定表该行的单元格数
     *
     * @param sheet    指定的表
     * @param rowIndex 行号
     * @return 此行含有内容的单元格数
     */
    public int getNumberOfCells(Sheet sheet, int rowIndex) {
        return this.defaultSheet.getRow(rowIndex).getPhysicalNumberOfCells();
    }

}
