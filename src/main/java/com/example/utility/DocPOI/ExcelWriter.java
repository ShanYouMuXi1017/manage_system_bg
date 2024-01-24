package com.example.utility.DocPOI;

import com.spire.xls.FileFormat;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.border.Border;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

/**
 * 写入Excel格式为xlsx的文档
 * <p>注: CellStyle多次设置会产生覆盖, 造成原本的效果消失</p>
 */
public class ExcelWriter {
    //创建xlsx格式的Excel工作簿
    private Workbook workbook = new XSSFWorkbook();
    //工作簿的第一个表(默认表)
    public Sheet sheet;

    /**
     * 无参构建Excel，默认只有一个表
     */
    public ExcelWriter() {
        this.sheet = workbook.createSheet();
    }

    /**
     * 根据传入的表名创建表, 构建Excel。可同时传入多个，默认使用第一个
     *
     * @param sheetNames 表名(可多个)
     */
    public ExcelWriter(String... sheetNames) {
        for (String sheetName : sheetNames) {
            this.workbook.createSheet(sheetName);
        }
        this.sheet = this.workbook.getSheet(sheetNames[0]);
    }

    /**
     * 通过表名获得创建的sheet
     *
     * @param sheetName 表名
     * @return 指定的sheet
     */
    public Sheet getSheet(String sheetName) {
        return this.workbook.getSheet(sheetName);
    }

    /**
     * 根据指定行列的单元格填充内容(第一张表)
     * (支持类型: byte|short|long|int|float|double|char|String|Boolean|Date|LocalDate|LocalDateTime|Calendar|RichTextString)
     *
     * @param rowIndex 单元格行号
     * @param colIndex 单元格列号
     * @param value    要填充的值
     * @param <T>      值value为多种类型
     * @return 处理后的单元格
     */
    public <T> Cell setValue(int rowIndex, int colIndex, T value) {
        return setValue(this.sheet, rowIndex, colIndex, value);
    }

    /**
     * 根据指定的sheet，为指定行列的单元格填充内容
     * (支持类型: byte|short|long|int|float|double|char|String|Boolean|Date|LocalDate|LocalDateTime|Calendar|RichTextString)
     *
     * @param sheet    指定的表
     * @param rowIndex 单元格行号
     * @param colIndex 单元格列号
     * @param value    要填充的值
     * @param <T>      值value为多种类型
     * @return 处理后的单元格
     */
    public <T> Cell setValue(Sheet sheet, int rowIndex, int colIndex, T value) {
        Row row = sheet.getRow(rowIndex);
        if (null == row) {
            row = sheet.createRow(rowIndex);
        }
        switch (value.getClass().getSimpleName()) {
            case "Byte":
            case "Long":
            case "Short":
            case "Integer":
            case "Float":
            case "Double":
                row.createCell(colIndex, CellType.NUMERIC)
                        .setCellValue((Double) value);
                break;
            case "Date":
                row.createCell(colIndex, CellType.NUMERIC)
                        .setCellValue((Date) value);
                break;
            case "LocalDateTime":
                row.createCell(colIndex, CellType.NUMERIC)
                        .setCellValue((LocalDateTime) value);
                break;
            case "LocalDate":
                row.createCell(colIndex, CellType.NUMERIC)
                        .setCellValue((LocalDate) value);
            case "Calendar":
                row.createCell(colIndex, CellType.NUMERIC)
                        .setCellValue((Calendar) value);
            case "Character":
            case "String":
                row.createCell(colIndex, CellType.STRING)
                        .setCellValue((String) value);
                break;
            case "RichTextString":
                row.createCell(colIndex, CellType.STRING)
                        .setCellValue((RichTextString) value);
                break;
            case "Boolean":
                row.createCell(colIndex, CellType.BOOLEAN)
                        .setCellValue((Boolean) value);
                break;
            default:
                row.createCell(colIndex, CellType.BLANK)
                        .setBlank();
        }
        return row.getCell(colIndex);
    }

    /**
     * 返回默认表的单元格, 以便自定义样式和信息
     *
     * @param rowIndex 指定行
     * @param colIndex 指定列
     * @return 需要的单元格
     */
    public Cell getCell(int rowIndex, int colIndex) {
        return getCell(this.sheet, rowIndex, colIndex);
    }

    /**
     * 返回指定表的单元格, 以便自定义样式和信息
     *
     * @param sheet    指定的表
     * @param rowIndex 指定行
     * @param colIndex 指定列
     * @return 需要的单元格
     */
    public Cell getCell(Sheet sheet, int rowIndex, int colIndex) {
        Row row = sheet.getRow(rowIndex);
        if (null == row) {
            row = sheet.createRow(rowIndex);
        }
        Cell cell = row.getCell(colIndex);
        if (null == cell) {
            cell = row.createCell(colIndex);
        }
        return cell;
    }

    /**
     * 获得一个CellStyle对象, 可自定义样式
     *
     * @return 工作簿的CellStyle对象
     */
    public CellStyle getCellStyle(Cell cell) {
        return cell.getCellStyle();
    }

    /**
     * 仅设置单元的内容水平居中 cell.setCellStyle(excelWriter.HorizontalCenter())
     */
    public CellStyle HorizontalCenter() {
        CellStyle cellStyle = this.workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        return cellStyle;
    }

    /**
     * 仅设置单元的内容垂直居中 cell.setCellStyle(excelWriter.VerticalCenter())
     */
    public CellStyle VerticalCenter() {
        CellStyle cellStyle = this.workbook.createCellStyle();
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        return cellStyle;
    }

    /**
     * 同时设置单元的内容 水平和垂直 居中 cell.setCellStyle(excelWriter.HorizontalVerticalCenter())
     */
    public CellStyle HorizontalVerticalCenter() {
        CellStyle cellStyle = this.workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        return cellStyle;
    }

    /**
     * 设置单元格边框类型
     * (可接受多个参数进行设置(顺时针):
     * 四个设置 上 右 下 左;
     * 三个设置 上 右左 下;
     * 两个设置 上下 左右;
     * 一个设置 上右下左)
     * <p>
     * ====>样式参考如下：
     * NONE(0): 无边框->即原本Excel的边框
     * THIN(1): 细边框
     * MEDIUM(2): 中等边框
     * DASHED(3): 虚线边框
     * DOTTED(4): 点线边框
     * THICK(5): 粗边框
     * DOUBLE(6): 双线边框
     * HAIR(7): 头发般细的边框
     * MEDIUM_DASHED(8): 中等虚线边框
     * DASH_DOT(9): 短线-点线边框
     * MEDIUM_DASH_DOT(10): 中等短线-点线边框
     * DASH_DOT_DOT(11): 短线-点-点线边框
     * MEDIUM_DASH_DOT_DOT(12): 中等短线-点-点线边框
     * SLANTED_DASH_DOT(13): 斜线短线-点线边框
     *
     * @param borderTypes 接收的可选参数
     * @return 边框样式对象
     */
    public CellStyle setBorderType(BorderStyle... borderTypes) {
        CellStyle cellBorderStyle = this.workbook.createCellStyle();

        if (null != borderTypes) {
            switch (borderTypes.length) {
                case 4:
                    cellBorderStyle.setBorderTop(borderTypes[0]);
                    cellBorderStyle.setBorderRight(borderTypes[1]);
                    cellBorderStyle.setBorderBottom(borderTypes[2]);
                    cellBorderStyle.setBorderLeft(borderTypes[3]);
                    break;
                case 3:
                    cellBorderStyle.setBorderTop(borderTypes[0]);
                    cellBorderStyle.setBorderRight(borderTypes[1]);
                    cellBorderStyle.setBorderBottom(borderTypes[1]);
                    cellBorderStyle.setBorderLeft(borderTypes[2]);
                    break;
                case 2:
                    cellBorderStyle.setBorderTop(borderTypes[0]);
                    cellBorderStyle.setBorderRight(borderTypes[1]);
                    cellBorderStyle.setBorderBottom(borderTypes[1]);
                    cellBorderStyle.setBorderLeft(borderTypes[0]);
                    break;
                case 1:
                    cellBorderStyle.setBorderTop(borderTypes[0]);
                    cellBorderStyle.setBorderRight(borderTypes[0]);
                    cellBorderStyle.setBorderBottom(borderTypes[0]);
                    cellBorderStyle.setBorderLeft(borderTypes[0]);
                    break;
                default:
                    break;
            }
        }
        return cellBorderStyle;
    }

    /**
     * 设置单元格边框样式(参数可选, 参数个数同borderStyle())
     * <p>
     * 颜色详见 IndexedColors
     *
     * @param borderColors 接收的可选参数
     * @return 边框颜色对象
     */
    public CellStyle setBorderStyle(CellStyle cellBorderType, IndexedColors... borderColors) {
        CellStyle borderStyle = cellBorderType;
        if (null != borderColors) {
            switch (borderColors.length) {
                case 4:
                    borderStyle.setTopBorderColor(borderColors[0].getIndex());
                    borderStyle.setRightBorderColor(borderColors[1].getIndex());
                    borderStyle.setBottomBorderColor(borderColors[2].getIndex());
                    borderStyle.setLeftBorderColor(borderColors[3].getIndex());
                    break;
                case 3:
                    borderStyle.setTopBorderColor(borderColors[0].getIndex());
                    borderStyle.setRightBorderColor(borderColors[1].getIndex());
                    borderStyle.setBottomBorderColor(borderColors[1].getIndex());
                    borderStyle.setLeftBorderColor(borderColors[2].getIndex());
                    break;
                case 2:
                    borderStyle.setTopBorderColor(borderColors[0].getIndex());
                    borderStyle.setRightBorderColor(borderColors[1].getIndex());
                    borderStyle.setBottomBorderColor(borderColors[1].getIndex());
                    borderStyle.setLeftBorderColor(borderColors[0].getIndex());
                    break;
                case 1:
                    borderStyle.setTopBorderColor(borderColors[0].getIndex());
                    borderStyle.setRightBorderColor(borderColors[0].getIndex());
                    borderStyle.setBottomBorderColor(borderColors[0].getIndex());
                    borderStyle.setLeftBorderColor(borderColors[0].getIndex());
                    break;
                default:
                    break;

            }
        }
        return borderStyle;
    }

    /**
     * 设置所有单元格黑色细边框
     */
    public void setEditAreaCellBlackTHINBorder() {
        Row row;
        Cell cell;
        for (int rowIndex = 0; rowIndex < this.sheet.getPhysicalNumberOfRows(); rowIndex++) {
            try {
                row = this.sheet.getRow(rowIndex);
            } catch (Exception e) {
                continue;
            }
            for (int cellIndex = 0; cellIndex < row.getPhysicalNumberOfCells(); cellIndex++) {
                try {
                    cell = row.getCell(cellIndex);
                    if (null == cell) {
                        cell = row.createCell(cellIndex);
                    }
                    CellStyle cellStyle = cell.getCellStyle();
                    cellStyle.setBorderTop(BorderStyle.THIN);
                    cellStyle.setBorderRight(BorderStyle.THIN);
                    cellStyle.setBorderBottom(BorderStyle.THIN);
                    cellStyle.setBorderLeft(BorderStyle.THIN);
                    //setBorderStyle(setBorderType(BorderStyle.THIN), IndexedColors.BLACK);
                } catch (Exception ignored) {
                }
            }
        }
    }

    /**
     * 合并单元格, 默认的第一个sheet
     * (依据Excel规则, 只保留合并后的第一个单元格内容)
     *
     * @param startRow 合并开始行
     * @param endRow   合并结束行
     * @param startCol 合并开始列
     * @param endCol   合并结束列
     * @return 合并状态
     */
    public boolean cellMerge(int startRow, int endRow, int startCol, int endCol) {
        return cellMerge(this.sheet, startRow, endRow, startCol, endCol);
    }

    /**
     * 合并单元格, 指定sheet
     * (依据Excel规则, 只保留合并后的第一个单元格内容)
     *
     * @param sheet    指定的表
     * @param startRow 合并开始行
     * @param endRow   合并结束行
     * @param startCol 合并开始列
     * @param endCol   合并结束列
     * @return 合并状态
     */
    public boolean cellMerge(Sheet sheet, int startRow, int endRow, int startCol, int endCol) {
        boolean status = true;
        try {
            sheet.addMergedRegion(new CellRangeAddress(startRow, endRow, startCol, endCol));
        } catch (Exception e) {
            status = false;
        }
        return status;
    }

    /**
     * 设置默认表某一列的宽度
     *
     * @param colIndex  第几列
     * @param charWidth 要设置的宽度，1 表示1个英文字符的宽度(一个汉字占两个英文字符宽度)
     */
    public void setColWidth(int colIndex, int charWidth) {
        setColWidth(this.sheet, colIndex, charWidth);
    }

    /**
     * 设置某一列的宽度
     *
     * @param sheet    要设置的表
     * @param colIndex 第几列
     * @param width    要设置的宽度，1 表示1个英文字符的宽度(一个汉字占两个英文字符宽度)
     */
    public void setColWidth(Sheet sheet, int colIndex, int width) {
        sheet.setColumnWidth(colIndex, (width + 1) * 256);
    }


    /**
     * 设置默认表某一行列的高度
     *
     * @param rowIndex 第几行
     * @param height   要设置的高度, 1 表示默认的一行宽
     */
    public void setRowHeight(int rowIndex, int height) {
        setRowHeight(this.sheet, rowIndex, height);
    }

    /**
     * 设置某一列的高度(height要大于1)
     *
     * @param sheet    要设置的表
     * @param rowIndex 第几行
     * @param height   要设置的高度, 1 表示默认的一行宽
     */
    public void setRowHeight(Sheet sheet, int rowIndex, int height) {
        if (height < 1) {
            return;
        }
        Row row = sheet.getRow(rowIndex);
        if (null == row) {
            row = sheet.createRow(rowIndex);
        }
        row.setHeightInPoints(height * 15);
    }

    /**
     * 保存Excel文档
     *
     * @param fileName 文件名，记得加上 .xlsx
     * @return 返回方法执行状态
     */
    public boolean saveFile(String fileName) {
        boolean status = false;
        try {
            if (!fileName.endsWith(".xlsx")) {
                throw new Exception("文件后缀不正确! (需要 .xlsx");
            }
            this.workbook.write(Files.newOutputStream(Paths.get(fileName)));
            status = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }

    /**
     * 实现将Excel文件转为PDF(默认Sheet0)
     * (推荐使用)
     *
     * @param excelPath excel文件路径(已有的)
     * @param PDFPath   PDF路径和名称(新建的)
     * @return 转换执行状态
     */
    public static boolean saveToPDF(String excelPath, String PDFPath) {
        return saveToPDF(excelPath, PDFPath, 0);
    }

    /**
     * 实现将Excel文件转为PDF
     * (推荐使用)
     *
     * @param excelPath  excel文件路径(已有的)
     * @param PDFPath    PDF路径和名称(新建的)
     * @param sheetIndex Excel的表索引(从0开始)
     * @return 转换执行状态
     */
    public static boolean saveToPDF(String excelPath, String PDFPath, int sheetIndex) {
        boolean status = true;
        try {
            if (!PDFPath.endsWith(".pdf")) {
                throw new Exception("PDF文件后缀不正确(需要.pdf)");
            }
            if (excelPath.endsWith(".xlsx")) {
                ExcelToPdfUtil.excelToPdf(excelPath, PDFPath, sheetIndex, ".xlsx");
            } else if (excelPath.endsWith(".xls")) {
                ExcelToPdfUtil.excelToPdf(excelPath, PDFPath, sheetIndex, ".xls");
            } else {
                throw new Exception("Excel文件后缀不正确(需要.xls|.xlsx)!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            status = false;
        }
        return status;
    }

    /**
     * 基于Spire.xls的简单封装, 实现将Excel文件转为PDF(可一次性将整个Excel多张表转为一个PDF)
     * ！(存在问题: 无法自适应Excel文字内容高度, 即转换的pdf部分内容可能不显示)
     *
     * @param excelPath excel文件路径(已有的)
     * @param PDFPath   PDF路径和名称(新建的)
     * @return 转换执行状态
     */
    public static boolean saveToPDFBySpire(String excelPath, String PDFPath) {
        boolean status = true;
        try {
            com.spire.xls.Workbook wb = new com.spire.xls.Workbook();

            //引入Excel文件
            wb.loadFromFile(excelPath);

            //设置单元格匹配pdf页面
            wb.getConverterSetting().setSheetFitToPage(true);
            wb.getConverterSetting().setSheetFitToWidth(true);

            //导出PDF文件
            wb.saveToFile(PDFPath, FileFormat.PDF);
        } catch (Exception e) {
            e.printStackTrace();
            status = false;
        }
        return status;
    }

}
