package com.example.utility.DocPOI;

import org.apache.poi.xwpf.usermodel.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * 读取word文档(注:仅支持.docx格式)
 * <a href="https://deepoove.com/poi-tl/apache-poi-guide.html">Apache POI Word文档参考阅读</a>
 */
public class WordReader {
    public XWPFDocument wordFile;

    /**
     * 构架wordReader对象
     *
     * @param wordFileInputStream word文档输入流(.docx格式)
     */
    public WordReader(FileInputStream wordFileInputStream) {
        try {
            this.wordFile = new XWPFDocument(wordFileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 返回Word文档内的所有段落, 一个回车算一个段落(表格/图片/页眉/页脚 不算段落)
     *
     * @return 所有段落的List集合
     */
    public List<XWPFParagraph> getAllParagraphs() {
        return this.wordFile.getParagraphs();
    }

    /**
     * 返回Word文档内的所有表格
     *
     * @return 所有表格的List集合
     */
    public List<XWPFTable> getAllTables() {
        return this.wordFile.getTables();
    }

    /**
     * 返回Word文档内的所有图片
     *
     * @return 所有图片的List集合
     */
    public List<XWPFPictureData> getAllPictures() {
        return this.wordFile.getAllPictures();
    }

    /**
     * 返回Word文档内的所有页眉
     *
     * @return 所有页眉的List集合
     */
    public List<XWPFHeader> getAllPageHeader() {
        return this.wordFile.getHeaderList();
    }

    /**
     * 返回Word文档内的所有页脚
     *
     * @return 所有页脚的List集合
     */
    public List<XWPFFooter> getAllPageFooter() {
        return this.wordFile.getFooterList();
    }

}
