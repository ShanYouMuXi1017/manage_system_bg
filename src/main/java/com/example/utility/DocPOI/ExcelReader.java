package com.example.utility.DocPOI;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * 读取Excel文档，支持xls和xlsx，如果需要进行更多关于表的操作，可调用对象的sheet进行
 */
public class ExcelReader {
    private FileInputStream excelDoc;
    private int sheetIndex = 0;
    public Sheet sheet;

    /**
     * 根据输入流确定表(表号默认为0, 即第一张表)
     *
     * @param excelDoc xls或xlsx格式的输入流
     */
    public ExcelReader(FileInputStream excelDoc) {
        this.excelDoc = excelDoc;
        try {
            getSheet();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * 根据输入流和表号确定表
     *
     * @param excelDoc   xls或xlsx格式的输入流
     * @param sheetIndex 表号(Excel文件中含有多个表, 可指定)
     */
    public ExcelReader(FileInputStream excelDoc, int sheetIndex) {
        this.excelDoc = excelDoc;
        this.sheetIndex = sheetIndex;
        try {
            getSheet();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void getSheet() throws IOException {
        this.sheet = WorkbookFactory.create(this.excelDoc).getSheetAt(this.sheetIndex);
    }

    /**
     * 根据行号和列号获取单元格的内容
     * 若单元格不含有任何数据，也未被定义将会报空指针异常
     *
     * @param rowIndex 单元格行号
     * @param colIndex 单元格列号
     * @return 此单元格存储的数据
     */
    public Object getValue(int rowIndex, int colIndex) {
        Cell cell = this.sheet.getRow(rowIndex).getCell(colIndex);
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
     * 获取该表的行数|含有内容的行数
     *
     * @return 有内容的行数
     */
    public int getNumberOfRows() {
        return this.sheet.getPhysicalNumberOfRows();
    }

    /**
     * 根据行号获取该行的单元格数
     *
     * @param rowIndex 行号
     * @return 此行含有内容的单元格数
     */
    public int getNumberOfCells(int rowIndex) {
        return this.sheet.getRow(rowIndex).getPhysicalNumberOfCells();
    }

}
