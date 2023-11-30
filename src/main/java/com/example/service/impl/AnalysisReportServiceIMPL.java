package com.example.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.mapper.CourseBasicInformationMAPPER;
import com.example.mapper.CourseExamineChildMethodsMAPPER;
import com.example.mapper.CourseExamineMethodsMAPPER;
import com.example.mapper.CourseTargetMAPPER;
import com.example.mapper.comprehensiveAnalyse.CourseAchievementAnalyseMAPPER;
import com.example.mapper.comprehensiveAnalyse.CourseScoreAnalyseMAPPER;
import com.example.mapper.comprehensiveAnalyse.CourseTargetAnalyseMAPPER;
import com.example.mapper.comprehensiveAnalyse.ExamPaperAnalyseReportMAPPER;
import com.example.mapper.examinePaper.*;
import com.example.object.CourseBasicInformation;
import com.example.object.CourseExamineChildMethods;
import com.example.object.CourseExamineMethods;
import com.example.object.CourseTarget;
import com.example.object.comprehensiveAnalyse.*;
import com.example.object.finalExamine.*;
import com.example.utility.DataExtend;
import com.example.utility.DataResponses;
import com.example.utility.export.ConvertToPdf;
import com.example.utility.export.export;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sini.com.spire.doc.*;
import com.sini.com.spire.doc.FileFormat;
import com.sini.com.spire.doc.documents.*;
import com.sini.com.spire.doc.fields.DocPicture;

import org.jfree.chart.*;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpServletResponse;

@Service
public class AnalysisReportServiceIMPL {
    @Autowired
    private CourseBasicInformationServiceIMPL courseBasicInformationServiceIMPL;
    @Autowired
    private CourseBasicInformationMAPPER courseBasicInformationMAPPER;
    @Autowired
    private CourseTargetMAPPER courseTargetMAPPER;
    @Autowired
    private CourseExamineMethodsMAPPER courseExamineMethodsMAPPER;
    @Autowired
    private CourseExamineChildMethodsMAPPER courseExamineChildMethodsMAPPER;
    @Autowired
    private CourseScoreAnalyseMAPPER courseScoreAnalyseMAPPER;
    @Autowired
    private CourseFinalExamPaperMAPPER courseFinalExamPaperMAPPER;
    @Autowired
    private CourseFinalExamPaperDetailMAPPER courseFinalExamPaperDetailMAPPER;
    @Autowired
    private CourseAchievementAnalyseMAPPER courseAchievementAnalyseMAPPER;
    @Autowired
    private StudentUsualScoreMAPPER studentUsualScoreMAPPER;
    @Autowired
    private StudentFinalScoreMAPPER studentFinalScoreMAPPER;
    @Autowired
    private StudentInformationMAPPER studentInformationMAPPER;
    @Autowired
    private CourseTargetAnalyseMAPPER courseTargetAnalyseMAPPER;
    @Autowired
    private ExamPaperAnalyseReportMAPPER examPaperAnalyseReportMAPPER;


    //在word文档中生成表格
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

        //给表格应用样式
//        table.applyStyle(DefaultTableStyle.Medium_Shading_1_Accent_1);

        //设置表格的右边框
        table.getTableFormat().getBorders().getRight().setBorderType(BorderStyle.Hairline);
        table.getTableFormat().getBorders().getRight().setLineWidth(1.0F);
        table.getTableFormat().getBorders().getRight().setColor(Color.BLACK);

        //设置表格的顶部边框
        table.getTableFormat().getBorders().getTop().setBorderType(BorderStyle.Hairline);
        table.getTableFormat().getBorders().getTop().setLineWidth(1.0F);
        table.getTableFormat().getBorders().getTop().setColor(Color.BLACK);

        //设置表格的左边框
        table.getTableFormat().getBorders().getLeft().setBorderType(BorderStyle.Hairline);
        table.getTableFormat().getBorders().getLeft().setLineWidth(1.0F);
        table.getTableFormat().getBorders().getLeft().setColor(Color.BLACK);

        //设置表格的底部边框
        table.getTableFormat().getBorders().getBottom().setBorderType(BorderStyle.Hairline);
        table.getTableFormat().getBorders().getBottom().setLineWidth(1.0F);
        table.getTableFormat().getBorders().getBottom().setColor(Color.BLACK);

        section.addParagraph();
        return table;
    }

    //在word文档中绘制空行
    private void nullRow(Section section, int row) {
        for (int i = 0; i < row; i++) {
            section.addParagraph();
        }
    }

    //在word表格中绘制空行
    private void nullRow(Table table, int row, int column, int count) {
        for (int i = 0; i < count; i++) {
            table.get(row, column).addParagraph();
        }
    }

    //在word文档中设置段落文字及其样式
    private void addText(Section section, String text, String style, Boolean center) {
        Paragraph para = section.addParagraph();
        para.appendText(text);
        para.applyStyle(style);
        if (center) {
            para.getFormat().setHorizontalAlignment(HorizontalAlignment.Center);
        }
    }

    //在word文档使用excel中生成图表
    private byte[] generateCharts(JSONArray jsonArray, int index) {
        try {
            XYSeriesCollection dataset = new XYSeriesCollection();
            XYSeries series = new XYSeries("学生达成度");

            for (Object item : jsonArray) {
                KeyValue value = new ObjectMapper().convertValue(item, KeyValue.class);
                series.add(value.getIndex(), value.getValue());
            }
            dataset.addSeries(series);


            //创建主题样式
            StandardChartTheme mChartTheme = new StandardChartTheme("CN");
            //设置标题字体
            mChartTheme.setExtraLargeFont(new Font("黑体", Font.BOLD, 20));
            //设置轴向字体
            mChartTheme.setLargeFont(new Font("黑体", Font.CENTER_BASELINE, 15));
            //设置图例字体
            mChartTheme.setRegularFont(new Font("黑体", Font.CENTER_BASELINE, 15));
            //应用主题样式
            ChartFactory.setChartTheme(mChartTheme);

            String s = "课程目标" + index + "达成情况";
            JFreeChart chart = ChartFactory.createScatterPlot(
                    s,
                    "",
                    "成绩达成度情况",
                    dataset,
                    PlotOrientation.VERTICAL,
                    true,
                    true,
                    false
            );

            XYPlot plot = (XYPlot) chart.getPlot();
            XYItemRenderer renderer = plot.getRenderer();
            renderer.setSeriesShape(0, new java.awt.geom.Ellipse2D.Double(-3, -3, 6, 6));
            renderer.setSeriesPaint(0, java.awt.Color.BLUE);

            plot.addRangeMarker(new ValueMarker(0.6, Color.RED, new BasicStroke(1)));

            // 获取 Y 轴对象
            ValueAxis yAxis = plot.getRangeAxis();
            // 设置 Y 轴的最小值为 0
            yAxis.setLowerBound(0);

            // Create a buffered image of the chart
            BufferedImage bufferedImage = chart.createBufferedImage(800, 300);
            // Convert the buffered image to a byte array
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ChartUtils.writeBufferedImageAsPNG(byteArrayOutputStream, bufferedImage);

            return byteArrayOutputStream.toByteArray();
        } catch (Exception exception) {
            return null;
        }
    }

    //在word文档中插入图表
    private void insertChart(Document document, byte[] bytes, Section section) {
        if (bytes != null) {
            DocPicture picture = new DocPicture(document);
            picture.loadImage(bytes);

            Paragraph para = section.addParagraph();
            para.getFormat().setHorizontalAlignment(HorizontalAlignment.Center);
            para.getChildObjects().insert(0, picture);
        }
    }

    //表格内容居中
    private void cellCenter(Table table) {
        for (int row = 0; row < table.getRows().getCount(); row++) {
            TableRow tableRow = table.getRows().get(row);
            tableRow.setHeight(30f);
            for (int col = 0; col < table.getDefaultColumnsNumber(); col++) {
                TableCell cell = tableRow.getCells().get(col);
                cell.getCellFormat().setVerticalAlignment(VerticalAlignment.Middle);
                for (int i = 0; i < cell.getParagraphs().getCount(); i++) {
                    cell.getParagraphs().get(i).getFormat().setHorizontalAlignment(HorizontalAlignment.Center);
                }
            }
        }
    }

    //生成报告文档version_2
    public ResponseEntity<byte[]> getReport(HttpServletResponse response, int courseId, int type) {
        try {
            Document document = new Document();

            //添加一个section
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
//            style1.getCharacterFormat().setTextColor(Color.BLUE);
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


            /*
                段落文字部分
             */
            //标题
            nullRow(section, 10);
            addText(section, "西南林业大学", "titleStyle", true);
            addText(section, "课程考核分析和目标达成情况分析报告", "titleStyle", true);

            //第二段
            //课程目标达成情况分析报告
            Section section2 = document.addSection();
            addText(section2, "西南林业大学", "titleStyle", true);
            addText(section2, "课程目标达成情况分析报告", "titleStyle", true);

            //添加表格
            section2.addParagraph();
            CourseBasicInformation courseBasicInformation = courseBasicInformationServiceIMPL.getById(courseId);
            Table table = generateTable(section2, 3, 4);
            table.get(0, 0).addParagraph().appendText("课程名称");
            table.get(0, 1).addParagraph().appendText(courseBasicInformation.getCourseName());
            table.get(0, 2).addParagraph().appendText("课程代码");
            table.get(0, 3).addParagraph().appendText(courseBasicInformation.getCourseCode());

            table.get(1, 0).addParagraph().appendText("任课教师姓名");
            table.get(1, 1).addParagraph().appendText(courseBasicInformation.getClassroomTeacher());
            table.get(1, 2).addParagraph().appendText("课程性质");
            table.get(1, 3).addParagraph().appendText(courseBasicInformation.getCourseType());

            table.get(2, 0).addParagraph().appendText("授课班级");
            table.get(2, 1).addParagraph().appendText(courseBasicInformation.getMajor());
            table.get(2, 2).addParagraph().appendText("学生人数");
            table.get(2, 3).addParagraph().appendText(String.valueOf(courseBasicInformation.getStudentsNum()));

            cellCenter(table);

            //获取课程目标
            QueryWrapper<CourseTarget> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("course_id", courseId);
            queryWrapper.orderByAsc("target_name");
            List<CourseTarget> courseTargets = courseTargetMAPPER.selectList(queryWrapper);

            addText(section2, "一、课程目标", "title2Style", false);
            addText(section2, "（1）课程目标", "title2Style", false);
            section2.addParagraph();
            addText(section2, "本课程的课程目标如下：", "contentStyle", false);
            for (CourseTarget target : courseTargets) {
                String targetName = target.getTargetName();
                String courseTarget = target.getCourseTarget();
                addText(section2, targetName + "：" + courseTarget, "contentStyle", false);
                nullRow(section2, 1);
            }


            addText(section2, "（2）课程目标对毕业要求的支撑", "title2Style", false);
            Table table2 = generateTable(section2, courseTargets.size() + 1, 3);
            table2.applyStyle(DefaultTableStyle.Medium_Shading_1_Accent_1);
            table2.get(0, 0).addParagraph().appendText("课程目标");
            table2.get(0, 1).addParagraph().appendText("支撑权重");
            table2.get(0, 2).addParagraph().appendText("毕业要求指标点");

            int i = 1;
            for (CourseTarget target : courseTargets) {
                if (i > courseTargets.size()) {
                    break;
                }
                String indicatorPoints = target.getIndicatorPoints();
                JSONArray jsonArray = JSONArray.parseArray(indicatorPoints);

                table2.get(i, 0).addParagraph().appendText(target.getTargetName());
                table2.get(i, 1).addParagraph().appendText(String.valueOf(target.getWeight()));

                String s = "";
                for (Object o : jsonArray) {
                    s += o.toString() + " ";
                }
                table2.get(i, 2).addParagraph().appendText(s);
                i++;
            }
            cellCenter(table2);

            addText(section2, "二、评分规则", "title2Style", false);
            nullRow(section2, 1);
            addText(section2, "（1）评价方法", "title2Style", false);
            section2.addParagraph();
            addText(section2, "课程目标达成考核与评价方式是成绩评定法。", "contentStyle", false);

            //获取考核评价方式
            QueryWrapper<CourseExamineMethods> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.eq("course_id", courseId);
            List<CourseExamineMethods> courseExamineMethods = courseExamineMethodsMAPPER.selectList(queryWrapper2);

            StringBuilder text = new StringBuilder("成绩评定法: 期末成绩=");
            for (int j = 0; j < courseExamineMethods.size(); j++) {
                String examineItem = courseExamineMethods.get(j).getExamineItem();
                int percentage = courseExamineMethods.get(j).getPercentage();
                String s = examineItem + percentage + "%";
                if (j == courseExamineMethods.size() - 1) {
                    text.append(s);
                    break;
                }
                text.append(s).append(" + ");
            }
            addText(section2, text.toString(), "contentStyle", false);

            nullRow(section2, 1);
            addText(section2, "（2）考核环节及成绩比例", "title2Style", false);
            section2.addParagraph();
            addText(section2, "评价比重", "contentStyle", false);

            List<List> courseExamineChildMethods = new ArrayList<>();
            for (CourseExamineMethods target : courseExamineMethods) {
                Integer id = target.getId();
                QueryWrapper<CourseExamineChildMethods> queryWrapper1 = new QueryWrapper<>();
                queryWrapper1.eq("course_examine_methods_id", id);
                List<CourseExamineChildMethods> courseExamineChildMethods1 = courseExamineChildMethodsMAPPER.selectList(queryWrapper1);
                courseExamineChildMethods.add(courseExamineChildMethods1);
            }
            //统计考核方式数量
            int rowNum = 0;
            for (List<CourseExamineChildMethods> item : courseExamineChildMethods) {
                rowNum += item.size();
            }

            Table table3 = generateTable(section2, rowNum + 1, 4);
            table3.applyStyle(DefaultTableStyle.Medium_Shading_1_Accent_1);
            table3.get(0, 0).addParagraph().appendText("考核项目");
            table3.get(0, 1).addParagraph().appendText("项目百分比");
            table3.get(0, 2).addParagraph().appendText("考核子项目");
            table3.get(0, 3).addParagraph().appendText("子项目百分比");

            int rowIndex = 1;
            int i1 = 0;
            for (List<CourseExamineChildMethods> item : courseExamineChildMethods) {
                int temp = rowIndex;
                table3.get(rowIndex, 0).addParagraph().appendText(courseExamineMethods.get(i1).getExamineItem());
                table3.get(rowIndex, 1).addParagraph().appendText(String.valueOf(courseExamineMethods.get(i1).getPercentage()));
                for (CourseExamineChildMethods childItem : item) {
                    table3.get(rowIndex, 2).addParagraph().appendText(childItem.getExamineChildItem());
                    table3.get(rowIndex, 3).addParagraph().appendText(String.valueOf(childItem.getChildPercentage()));
                    rowIndex++;
                }
                table3.applyVerticalMerge(0, temp, rowIndex - 1);
                table3.applyVerticalMerge(1, temp, rowIndex - 1);

                i1++;
            }
            cellCenter(table3);

            //计分方法与对应的课程目标
//            Section section3 = document.addSection();
            addText(section2, "计分方法与对应的课程目标", "contentStyle", false);

            rowNum = courseTargets.size() * courseExamineMethods.size();
            Table table4 = generateTable(section2, rowNum + 1, 4);
            table4.applyStyle(DefaultTableStyle.Medium_Shading_1_Accent_1);
            table4.get(0, 0).addParagraph().appendText("课程教学目标");
            table4.get(0, 1).addParagraph().appendText("预设的学习任务");
            table4.get(0, 2).addParagraph().appendText("观测点");
            table4.get(0, 3).addParagraph().appendText("标准计分");

            rowIndex = 1;
            for (CourseTarget courseTarget : courseTargets) {
                int temp = rowIndex;
                table4.get(rowIndex, 0).addParagraph().appendText(courseTarget.getTargetName());
                for (CourseExamineMethods courseExamineMethod : courseExamineMethods) {
                    table4.get(rowIndex, 1).addParagraph().appendText(courseExamineMethod.getExamineItem());

                    QueryWrapper<CourseExamineChildMethods> queryWrapper1 = new QueryWrapper<>();
                    queryWrapper1.eq("course_examine_methods_id", courseExamineMethod.getId());
                    List<CourseExamineChildMethods> courseExamineChildMethods1 = courseExamineChildMethodsMAPPER.selectList(queryWrapper1);

                    String s = "";
                    int n = 0;
                    if (courseExamineMethod.getExamineItem().contains("平时")) {
                        for (CourseExamineChildMethods item : courseExamineChildMethods1) {
                            //判断是否含有该课程目标
                            JSONArray jsonArray = JSONArray.parseArray(item.getCourseTarget());
                            for (Object o : jsonArray) {
                                if (courseTarget.getTargetName().contains(o.toString())) {
                                    s += item.getExamineChildItem() + " ";
                                    n += item.getChildPercentage();
                                }
                            }
                        }
                        table4.get(rowIndex, 2).addParagraph().appendText(s);
                        table4.get(rowIndex, 3).addParagraph().appendText(String.valueOf(n));

                    } else if (courseExamineMethod.getExamineItem().contains("期末")) {
                        for (CourseExamineChildMethods item : courseExamineChildMethods1) {
                            if (item.getExamineChildItem().contains("试卷")) {
                                QueryWrapper<CourseFinalExamPaper> queryWrapper3 = new QueryWrapper<>();
                                queryWrapper3.eq("exam_child_method_id", item.getId());
                                List<CourseFinalExamPaper> courseFinalExamPapers = courseFinalExamPaperMAPPER.selectList(queryWrapper3);

                                String itemName;
                                for (CourseFinalExamPaper item2 : courseFinalExamPapers) {
                                    itemName = item2.getItemName();

                                    QueryWrapper<CourseFinalExamPaperDetail> queryWrapper4 = new QueryWrapper<>();
                                    queryWrapper4.eq("primary_id", item2.getId());
                                    List<CourseFinalExamPaperDetail> courseFinalExamPaperDetails = courseFinalExamPaperDetailMAPPER.selectList(queryWrapper4);

                                    for (CourseFinalExamPaperDetail item3 : courseFinalExamPaperDetails) {
                                        JSONArray jsonArray = JSONArray.parseArray(item3.getCourseTarget());
                                        for (Object o : jsonArray) {
                                            if (courseTarget.getTargetName().contains(o.toString())) {
                                                itemName += item3.getTitleNumber() + " ";
                                                n += item3.getScore();
                                            }
                                        }
                                    }
                                    table4.get(rowIndex, 2).addParagraph().appendText(itemName);
                                }
                                table4.get(rowIndex, 3).addParagraph().appendText(String.valueOf(n));
                                n = 0;
                            }
                        }
                    }


                    rowIndex++;
                }
                table4.applyVerticalMerge(0, temp, rowIndex - 1);
            }
            cellCenter(table4);

            //评价标准
            addText(section2, "（3）评分标准", "title2Style", false);
            Table table5 = generateTable(section2, 5, 3);
            table5.applyStyle(DefaultTableStyle.Medium_Shading_1_Accent_1);
            table5.get(0, 0).addParagraph().appendText("考核方式");
            table5.get(0, 1).addParagraph().appendText("评价标准");
            table5.get(0, 2).addParagraph().appendText("得分");

            table5.get(1, 2).addParagraph().appendText("80～100");
            table5.get(2, 2).addParagraph().appendText("65～79");
            table5.get(3, 2).addParagraph().appendText("45～64");
            table5.get(4, 2).addParagraph().appendText("0～44");

            table5.get(1, 1).addParagraph().appendText("所表述的概念充分、严谨、准确;计算步骤完整,结果准确,运用的概念、理论和公式准确;准时上交、作业本整洁、书写工整");
            table5.get(2, 1).addParagraph().appendText("所表述的概念较充分、正确;计算步骤较完整,结果正确, 运用的概念、理论和公式较准确;按时上交、作业本有涂改、书写较工整");
            table5.get(3, 1).addParagraph().appendText("所表述的概念基本正确,个别有误;计算步骤不太完整, 结果基本正确,运用的概念、理论和公式基本准确;按时上交、作业本有涂改、书写潦草");
            table5.get(4, 1).addParagraph().appendText("所表述的概念错误较多;计算步骤不完整,结果不正确, 运用的概念、理论和公式不准确;未准时上交、书写潦草");

            table5.applyVerticalMerge(0, 1, 4);
            table5.get(1, 0).addParagraph().appendText("考核方式");
//            cellCenter(table5);


            addText(section2, "（4）考核成绩情况", "title2Style", false);
            addText(section2, "学生考核成绩见附件，无考核成绩调整情况。", "contentStyle", false);
            nullRow(section2, 3);

            addText(section2, "三、课程目标达成情况分析", "title2Style", false);
            nullRow(section2, 1);
            addText(section2, "（1）达成情况", "title2Style", false);
            section2.addParagraph();
            addText(section2, "\t依据" + courseBasicInformation.getClassName() + "班《" + courseBasicInformation.getCourseName() + "》原始考试成绩中学生个体所对应的各观测点数据分析，可得各课程目标考试成绩达成情况散点分布：", "contentStyle", false);


            //获取绘制表格所需的数据
            QueryWrapper<CourseAchievementAnalyse> queryWrapper5 = new QueryWrapper<>();
            queryWrapper5.eq("course_id", courseId);
            CourseAchievementAnalyse courseAchievementAnalyse = courseAchievementAnalyseMAPPER.selectOne(queryWrapper5);

            //绘制图表
            JSONArray jsonArray = JSONArray.parseArray(courseAchievementAnalyse.getValue());
            for (int j = 0; j < jsonArray.size(); j++) {

                JSONArray jsonArray1 = JSONArray.parseArray(jsonArray.get(j).toString());
                byte[] bytes = generateCharts(jsonArray1, j + 1);

                insertChart(document, bytes, section2);
                nullRow(section2, 2);
            }

            Section section3 = document.addSection();
            addText(section3, "（2）课程目标达成情况", "title2Style", false);
            section3.addParagraph();
            addText(section3, "本课程各目标评价结果如下：", "contentStyle", false);

            rowNum = courseTargets.size() * courseExamineMethods.size();
            Table table6 = generateTable(section3, rowNum + 2, 7);
            table6.applyStyle(DefaultTableStyle.Medium_Shading_1_Accent_1);
            table6.get(0, 0).addParagraph().appendText("课程教学目标");
            table6.get(0, 1).addParagraph().appendText("预设的学习任务");
            table6.get(0, 2).addParagraph().appendText("观测点");
            table6.get(0, 3).addParagraph().appendText("标准计分");
            table6.get(0, 4).addParagraph().appendText("学生所得平均分");
            table6.get(0, 5).addParagraph().appendText("观测点达成度");
            table6.get(0, 6).addParagraph().appendText("课程目标达成度");

            rowIndex = 1;

            List<StudentUsualScore> studentUsualScores = studentUsualScoreMAPPER.getAllStudent(courseId);
            List<StudentFinalScore> studentFinalScores = studentFinalScoreMAPPER.getAllStudent(courseId);

            QueryWrapper<CourseTargetAnalyse> queryWrapper6 = new QueryWrapper<>();
            queryWrapper6.eq("course_id", courseId);
            List<CourseTargetAnalyse> courseTargetAnalyses = courseTargetAnalyseMAPPER.selectList(queryWrapper6);

            String resultStr = "";
            double result = 0;

            for (CourseTarget courseTarget : courseTargets) {
                int temp = rowIndex;
                table6.get(rowIndex, 0).addParagraph().appendText(courseTarget.getTargetName());

                CourseTargetAnalyse currentTarget = null;
                for (CourseTargetAnalyse item : courseTargetAnalyses) {
                    if (item.getTargetName().equals(courseTarget.getTargetName())) {
                        currentTarget = item;
                    }
                }
                double usualAchievement = 0;
                double finalAchievement = 0;
                double usualPercentage = 1;
                double finalPercentage = 1;

                for (CourseExamineMethods courseExamineMethod : courseExamineMethods) {
                    table6.get(rowIndex, 1).addParagraph().appendText(courseExamineMethod.getExamineItem());

                    QueryWrapper<CourseExamineChildMethods> queryWrapper1 = new QueryWrapper<>();
                    queryWrapper1.eq("course_examine_methods_id", courseExamineMethod.getId());
                    List<CourseExamineChildMethods> courseExamineChildMethods1 = courseExamineChildMethodsMAPPER.selectList(queryWrapper1);

                    String s = "";
                    int n = 0;
                    if (courseExamineMethod.getExamineItem().contains("平时")) {
//                        boolean[] boolArray = new boolean[courseExamineChildMethods1.size()];
                        List<DataExtend> keyValues = new ArrayList<>();

                        usualPercentage = courseExamineMethod.getPercentage() * 0.01;

//                        int childMethodsIndex = 0;
                        for (CourseExamineChildMethods item : courseExamineChildMethods1) {
                            //判断是否含有该课程目标
                            boolean isHave = false;
                            JSONArray jsonArray2 = JSONArray.parseArray(item.getCourseTarget());
                            for (Object o : jsonArray2) {
                                if (courseTarget.getTargetName().contains(o.toString())) {
                                    //标准计分
                                    s += item.getExamineChildItem() + " ";
                                    isHave = true;
                                    n += item.getChildPercentage();
                                }
                            }
                            //构建关系数组
                            keyValues.add(new DataExtend(String.valueOf(isHave), String.valueOf(item.getChildPercentage())));
//                            boolArray[childMethodsIndex] = isHave;
//                            childMethodsIndex++;
                        }

                        double sum = 0;
                        for (StudentUsualScore items : studentUsualScores) {
                            String[] strings = export.stringToOneDArray(items.getScoreDetails());
//                            JSONArray jsonArray1 = JSONArray.parseArray(items.getScoreDetails());
                            for (int j = 0; j < strings.length; j++) {
                                if (Boolean.parseBoolean(keyValues.get(j).getMessage())) {
                                    if (Objects.equals(strings[j], "")) {
                                        sum += 0;
                                    } else {
                                        sum += Double.parseDouble(strings[j]) * Integer.parseInt(keyValues.get(j).getData()) * 0.01;
                                    }
                                }
                            }
                        }

                        double avg = sum / studentUsualScores.size();
                        double v = export.doubleFormat(avg, 2);
                        double v2 = 0;
                        if (!(n == 0)) {
                            v2 = export.doubleFormat(v / n, 2);
                            usualAchievement = v2 * usualPercentage;
                        }

                        table6.get(rowIndex, 2).addParagraph().appendText(s);
                        table6.get(rowIndex, 3).addParagraph().appendText(String.valueOf(n));
                        table6.get(rowIndex, 4).addParagraph().appendText(String.valueOf(v));
                        table6.get(rowIndex, 5).addParagraph().appendText(String.valueOf(v2));

                    } else if (courseExamineMethod.getExamineItem().contains("期末")) {
                        finalPercentage = courseExamineMethod.getPercentage() * 0.01;
                        for (CourseExamineChildMethods item : courseExamineChildMethods1) {
                            if (item.getExamineChildItem().contains("试卷")) {
                                QueryWrapper<CourseFinalExamPaper> queryWrapper3 = new QueryWrapper<>();
                                queryWrapper3.eq("exam_child_method_id", item.getId());
                                List<CourseFinalExamPaper> courseFinalExamPapers = courseFinalExamPaperMAPPER.selectList(queryWrapper3);

                                String itemName;
                                for (CourseFinalExamPaper item2 : courseFinalExamPapers) {
                                    itemName = item2.getItemName();

                                    QueryWrapper<CourseFinalExamPaperDetail> queryWrapper4 = new QueryWrapper<>();
                                    queryWrapper4.eq("primary_id", item2.getId());
                                    List<CourseFinalExamPaperDetail> courseFinalExamPaperDetails = courseFinalExamPaperDetailMAPPER.selectList(queryWrapper4);

                                    for (CourseFinalExamPaperDetail item3 : courseFinalExamPaperDetails) {
                                        JSONArray jsonArray3 = JSONArray.parseArray(item3.getCourseTarget());
                                        for (Object o : jsonArray3) {
                                            if (courseTarget.getTargetName().contains(o.toString())) {
                                                itemName += item3.getTitleNumber() + " ";
                                                n += item3.getScore();
                                            }
                                        }
                                    }
                                    table6.get(rowIndex, 2).addParagraph().appendText(itemName);
                                }

                                double sum = 0;
                                String matrix = currentTarget.getMatrix();
                                JSONArray jsonArray1 = JSONArray.parseArray(matrix);


                                for (StudentFinalScore items : studentFinalScores) {
                                    List<String> strings = export.toArray(export.stringTo2DArray(items.getScoreDetails()));
                                    for (int j = 0; j < strings.size(); j++) {
                                        if (Boolean.parseBoolean(jsonArray1.get(j).toString()) && !strings.get(j).equals("")) {
                                            sum += Double.parseDouble(strings.get(j));
                                        }
                                    }
                                }

                                double v = export.doubleFormat(sum / studentFinalScores.size(), 2);
                                double v2 = 0;

                                if (!(n == 0)) {
                                    v2 = export.doubleFormat(v / n, 2);
                                    finalAchievement = v2 * finalPercentage;
                                }


                                table6.get(rowIndex, 3).addParagraph().appendText(String.valueOf(n));
                                table6.get(rowIndex, 4).addParagraph().appendText(String.valueOf(v));
                                table6.get(rowIndex, 5).addParagraph().appendText(String.valueOf(v2));

                                n = 0;
                            }
                        }
                    }


                    rowIndex++;
                }

                //防止出现平时或者期末达成度为0
                if (usualAchievement == 0) {
                    finalAchievement = finalAchievement / finalPercentage;
                } else if (finalAchievement == 0) {
                    usualAchievement = usualAchievement / usualPercentage;
                }

                double v = export.doubleFormat(usualAchievement + finalAchievement, 2);
                table6.applyVerticalMerge(0, temp, rowIndex - 1);
                table6.get(rowIndex - 1, 6).addParagraph().appendText(String.valueOf(v));
                table6.applyVerticalMerge(6, temp, rowIndex - 1);

                resultStr += v + " x " + courseTarget.getWeight() + " + ";
                result += v * courseTarget.getWeight();
            }

            result = export.doubleFormat(result, 2);
            resultStr = resultStr.substring(0, resultStr.length() - 2);
            resultStr = resultStr + " = " + result;

            table6.get(rowIndex, 0).addParagraph().appendText("总课程目标达成度");
            table6.applyHorizontalMerge(rowIndex, 0, 1);
            table6.get(rowIndex, 2).addParagraph().appendText(resultStr);
            table6.applyHorizontalMerge(rowIndex, 2, 6);
            cellCenter(table6);


            nullRow(section3, 5);
            addText(section3, "四、面向学生的课程目标达成度评价问卷调查结果", "title2Style", false);
            addText(section3, "\t在调查问卷中，对课程目标达成情况的评价为“完全达成、较好达成、基本达成、未达成”四个等级。针对本课程目标达成情况的问卷调查结果如图5所示。", "contentStyle", false);
            nullRow(section3, 20);
            addText(section3, "五、课程持续改进措施", "title2Style", false);
            addText(section3, "（1）近两学年教学课程目标达成度对比", "title2Style", false);

            Table table7 = generateTable(section3, 4, courseTargets.size());
            table7.applyStyle(DefaultTableStyle.Medium_Shading_1_Accent_1);
            table7.get(0, 0).addParagraph().appendText("授课时间");
            table7.applyVerticalMerge(0, 0, 1);
            table7.get(0, 1).addParagraph().appendText("课程目标达成度");
            table7.applyHorizontalMerge(0, 1, courseTargets.size() - 1);

            table7.get(2, 0).addParagraph().appendText("XX学年第X学期");
            table7.get(3, 0).addParagraph().appendText("XX学年第X学期");
            cellCenter(table7);


            addText(section3, "（2）存在问题及持续改进", "title2Style", false);
            Table table8 = generateTable(section3, 5, 3);
            table8.get(0, 0).addParagraph().appendText("上轮教学");
            table8.get(1, 0).addParagraph().appendText("本轮教学");
            table8.applyVerticalMerge(0, 1, 4);
            cellCenter(table8);

            addText(section3, "任课教师签字", "contentStyle", false);
            section3.addParagraph();
            addText(section3, "系（教研室）主任签字", "contentStyle", false);
            section3.addParagraph();
            addText(section3, "填写时间", "contentStyle", false);

            Section section4 = document.addSection();
            addText(section4, "附件:学生考核成绩", "contentStyle", false);
            List<StudentComprehensiveScore> comprehensiveScore = studentInformationMAPPER.getComprehensiveScore(courseId);

            Table table9 = generateTable(section4, comprehensiveScore.size() + 1, 6);
            table9.applyStyle(DefaultTableStyle.Medium_Shading_1_Accent_1);
            table9.get(0, 0).addParagraph().appendText("学号");
            table9.get(0, 1).addParagraph().appendText("姓名");
            table9.get(0, 2).addParagraph().appendText("班级");
            table9.get(0, 3).addParagraph().appendText("平时成绩");
            table9.get(0, 4).addParagraph().appendText("期末卷面成绩");
            table9.get(0, 5).addParagraph().appendText("综合成绩");

            rowIndex = 1;
            for (StudentComprehensiveScore item : comprehensiveScore) {
                table9.get(rowIndex, 0).addParagraph().appendText(item.getStudentNumber());
                table9.get(rowIndex, 1).addParagraph().appendText(item.getStudentName());
                table9.get(rowIndex, 2).addParagraph().appendText(item.getClassName());
                table9.get(rowIndex, 3).addParagraph().appendText(String.valueOf(item.getUsualScore()));
                table9.get(rowIndex, 4).addParagraph().appendText(String.valueOf(item.getFinalScore()));
                table9.get(rowIndex, 5).addParagraph().appendText(String.valueOf(item.getComprehensiveScore()));
                rowIndex++;
            }
            cellCenter(table9);


            byte[] Bytes = null;
            String fileName = "";
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            response.reset();

            if (type == 1) {
                document.saveToStream(outputStream, FileFormat.Docx);
                fileName = "课程目标达成评价分析报告.docx";
                response.setContentType("application/msword");

            } else if (type == 2) {
//                File directory = new File("");
//                String dirPath = directory.getCanonicalPath() + "/temp/";
//                String fileNameText = dirPath + "temp.docx";
//                File fileRealPath = new File(dirPath);
//                if (!fileRealPath.exists()) {
//                    if (!fileRealPath.mkdirs()) {
//                        return null;
//                    }
//                }
//                document.saveToFile(fileNameText, FileFormat.Docx);
//
//                if (ConvertToPdf.convertWordToPdf(fileNameText, dirPath)) {
//                    Path path = Paths.get(dirPath + "temp.pdf");
//                    Bytes = Files.readAllBytes(path);
//                }
                document.saveToStream(outputStream, FileFormat.PDF);

                fileName = "课程目标达成评价分析报告.pdf";
                response.setContentType("application/pdf");
            }
            Bytes = outputStream.toByteArray();
            outputStream.close();

            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-Disposition", "attachment;fileName=" + new String(fileName.getBytes(), "iso-8859-1"));


            return ResponseEntity.ok()
                    .body(Bytes);
        } catch (IOException ignored) {
            return null;
        }
    }

    //生成课程试卷分析报告表
    public ResponseEntity<byte[]> getReport3(HttpServletResponse response, int courseId, int type) {
        try {
            Document document = new Document();

            //添加一个section
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
//            style1.getCharacterFormat().setTextColor(Color.BLUE);
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


            /*
                段落文字部分
             */
            //标题
            addText(section, "试卷分析报告", "title2Style", true);

            //添加表格数据
            CourseBasicInformation courseBasicInformation = courseBasicInformationServiceIMPL.getById(courseId);
            QueryWrapper<CourseScoreAnalyse> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("course_id", courseId);
            CourseScoreAnalyse scoreAnalyse = courseScoreAnalyseMAPPER.selectOne(queryWrapper);

            addText(section, courseBasicInformation.getTermStart() + "-" + courseBasicInformation.getTermEnd() + "学年第" + courseBasicInformation.getTerm() + "学期", "contentStyle", true);

            addText(section, "课程名称: " + courseBasicInformation.getCourseName() + "\t\t课程代码: XXXXX\t\t课程性质: " + courseBasicInformation.getCourseNature() + "\t\t学分: 4\t", "contentStyle", true);

            Table table = generateTable(section, 9, 7);
            // 合并单元格
//            table.applyHorizontalMerge(2, 3, 5);
            table.get(0, 0).addParagraph().appendText("任课教师");
            table.get(0, 1).addParagraph().appendText(courseBasicInformation.getClassroomTeacher());
            table.get(0, 2).addParagraph().appendText("授课班级");
            table.get(0, 3).addParagraph().appendText(courseBasicInformation.getClassName());
            table.applyHorizontalMerge(0, 3, 6);

            table.get(1, 0).addParagraph().appendText("考试形式");
            table.get(1, 1).addParagraph().appendText("");
            table.get(1, 2).addParagraph().appendText("考试日期");
            table.get(1, 3).addParagraph().appendText("");
            table.applyHorizontalMerge(1, 3, 4);
            table.get(1, 5).addParagraph().appendText("人数");
            table.get(1, 6).addParagraph().appendText(String.valueOf(courseBasicInformation.getStudentsNum()));

            table.get(2, 0).addParagraph().appendText("试卷成绩");
            table.applyVerticalMerge(0, 2, 5);
            table.get(2, 1).addParagraph().appendText("成绩等级");
            table.get(2, 2).addParagraph().appendText("< 60");
            table.get(2, 3).addParagraph().appendText("60 - 69");
            table.get(2, 4).addParagraph().appendText("70 - 79");
            table.get(2, 5).addParagraph().appendText("80 - 89");
            table.get(2, 6).addParagraph().appendText("90 - 100");

            table.get(3, 1).addParagraph().appendText("人数");
            table.get(3, 2).addParagraph().appendText(String.valueOf(scoreAnalyse.getFailed()));
            table.get(3, 3).addParagraph().appendText(String.valueOf(scoreAnalyse.getPass()));
            table.get(3, 4).addParagraph().appendText(String.valueOf(scoreAnalyse.getGood()));
            table.get(3, 5).addParagraph().appendText(String.valueOf(scoreAnalyse.getGreat()));
            table.get(3, 6).addParagraph().appendText(String.valueOf(scoreAnalyse.getSuperior()));

            table.get(4, 1).addParagraph().appendText("所占比例");
            double i = export.doubleFormat(scoreAnalyse.getFailed() * 1.0 / scoreAnalyse.getStudentNum(), 2);
            table.get(4, 2).addParagraph().appendText(String.valueOf(i));

            i = export.doubleFormat(scoreAnalyse.getPass() * 1.0 / scoreAnalyse.getStudentNum(), 2);
            table.get(4, 3).addParagraph().appendText(String.valueOf(i));

            i = export.doubleFormat(scoreAnalyse.getGood() * 1.0 / scoreAnalyse.getStudentNum(), 2);
            table.get(4, 4).addParagraph().appendText(String.valueOf(i));

            i = export.doubleFormat(scoreAnalyse.getGreat() * 1.0 / scoreAnalyse.getStudentNum(), 2);
            table.get(4, 5).addParagraph().appendText(String.valueOf(i));

            i = export.doubleFormat(scoreAnalyse.getSuperior() * 1.0 / scoreAnalyse.getStudentNum(), 2);
            table.get(4, 6).addParagraph().appendText(String.valueOf(i));

            table.get(5, 1).addParagraph().appendText("平均值");
            table.get(5, 2).addParagraph().appendText(String.valueOf(scoreAnalyse.getAverageScore()));
            table.get(5, 3).addParagraph().appendText("最高分");
            table.get(5, 4).addParagraph().appendText(String.valueOf(scoreAnalyse.getMaxScore()));
            table.get(5, 5).addParagraph().appendText("最低分");
            table.get(5, 6).addParagraph().appendText(String.valueOf(scoreAnalyse.getMinScore()));
            cellCenter(table);

            //老师填写部分
            QueryWrapper<ExamPaperAnalyseReport> queryWrappern = new QueryWrapper<>();
            queryWrappern.eq("course_id", courseId);
            ExamPaperAnalyseReport examPaperAnalyseReport = examPaperAnalyseReportMAPPER.selectOne(queryWrappern);

            nullRow(table, 6, 0, 9);
            table.get(6, 0).addParagraph().appendText("试卷情况分析");
            table.get(6, 1).addParagraph().appendText(examPaperAnalyseReport.getExamPaperSituationAnalyse());
            table.applyHorizontalMerge(6, 1, 6);
            nullRow(table, 6, 0, 9);

            nullRow(table, 7, 0, 9);
            table.get(7, 0).addParagraph().appendText("改进措施");
            table.get(7, 1).addParagraph().appendText(examPaperAnalyseReport.getImprovementActions());
            table.applyHorizontalMerge(7, 1, 6);
            nullRow(table, 7, 0, 9);

            table.get(8, 0).addParagraph().appendText("教研室主任（签字）：");
            table.applyHorizontalMerge(8, 0, 1);
            table.applyHorizontalMerge(8, 2, 3);

            table.get(8, 4).addParagraph().appendText("日期：");
            table.applyHorizontalMerge(8, 4, 6);
            table.getRows().get(8).setHeight(30f);

            byte[] Bytes;
            String fileName = "";
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            response.reset();

            if (type == 1) {
                document.saveToStream(outputStream, FileFormat.Docx);
                fileName = "课程试卷分析报告.docx";

            } else if (type == 2) {
                document.saveToStream(outputStream, FileFormat.PDF);
                fileName = "课程试卷分析报告.pdf";
            }
            Bytes = outputStream.toByteArray();
            outputStream.close();

            response.setCharacterEncoding("utf-8");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;fileName=" + new String(fileName.getBytes(), "iso-8859-1"));

            //            return ResponseEntity.ok().headers(headers).body(Bytes);
            return ResponseEntity.ok().body(Bytes);
        } catch (IOException ignored) {
            return null;
        }
    }

    //生成课程教学小结表
    public ResponseEntity<byte[]> getReport4(HttpServletResponse response, int courseId, int type) {
        try {
            Document document = new Document();

            //添加一个section
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
//            style1.getCharacterFormat().setTextColor(Color.BLUE);
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


            /*
                段落文字部分
             */
            //标题
            addText(section, "西南林业大学课程教学小结表", "title2Style", true);

            //添加表格数据
            CourseBasicInformation courseBasicInformation = courseBasicInformationServiceIMPL.getById(courseId);

            Table table = generateTable(section, 10, 6);
            // 合并单元格
//            table.applyHorizontalMerge(2, 3, 5);
            table.get(0, 0).addParagraph().appendText("授课学期");
            table.get(0, 1).addParagraph().appendText(courseBasicInformation.getTermStart() + "-" + courseBasicInformation.getTermEnd() + "-" + courseBasicInformation.getTerm());
            table.get(0, 2).addParagraph().appendText("课程名称");
            table.get(0, 3).addParagraph().appendText(courseBasicInformation.getCourseName());
            table.applyHorizontalMerge(0, 3, 5);

            table.get(1, 0).addParagraph().appendText("总学时");
            table.get(1, 1).addParagraph().appendText(String.valueOf(courseBasicInformation.getTheoreticalHours()));
            table.get(1, 2).addParagraph().appendText("实验学时");
            table.get(1, 3).addParagraph().appendText(String.valueOf(courseBasicInformation.getLabHours()));
            table.get(1, 4).addParagraph().appendText("实习天数");
            table.get(1, 5).addParagraph().appendText("");

            table.get(2, 0).addParagraph().appendText("开课院系");
            table.get(2, 1).addParagraph().appendText("大数据与智能工程系");
            table.get(2, 2).addParagraph().appendText("授课班级");
            table.get(2, 3).addParagraph().appendText(courseBasicInformation.getClassName());
            table.get(2, 4).addParagraph().appendText("学生人数");
            table.get(2, 5).addParagraph().appendText(String.valueOf(courseBasicInformation.getStudentsNum()));

            table.get(3, 0).addParagraph().appendText("多媒体教学");
            table.get(3, 1).addParagraph().appendText("");
            table.get(3, 2).addParagraph().appendText("双语教学");
            table.get(3, 3).addParagraph().appendText("");
            table.get(3, 4).addParagraph().appendText("考核方式");
            table.get(3, 5).addParagraph().appendText("统考");

            table.get(4, 0).addParagraph().appendText("所用教材");
            table.get(4, 1).addParagraph().appendText(courseBasicInformation.getTextBook());
            table.applyHorizontalMerge(4, 1, 3);
            table.get(4, 4).addParagraph().appendText("出版日期");
            table.get(4, 3).addParagraph().appendText("");

            cellCenter(table);

            //老师填写部分
            QueryWrapper<ExamPaperAnalyseReport> queryWrappern = new QueryWrapper<>();
            queryWrappern.eq("course_id", courseId);
            ExamPaperAnalyseReport examPaperAnalyseReport = examPaperAnalyseReportMAPPER.selectOne(queryWrappern);

            nullRow(table, 5, 0, 10);
            table.get(5, 0).addParagraph().appendText("课程教学总结");
            table.get(5, 1).addParagraph().appendText(examPaperAnalyseReport.getCourseTeachingSummary());
            table.applyHorizontalMerge(5, 1, 5);
            nullRow(table, 5, 0, 10);

            nullRow(table, 6, 0, 10);
            table.get(6, 0).addParagraph().appendText("今后改革设想");
            table.get(6, 1).addParagraph().appendText(examPaperAnalyseReport.getReformAssumption());
            table.applyHorizontalMerge(6, 1, 5);
            nullRow(table, 6, 0, 10);

            table.get(7, 0).addParagraph().appendText("填报人");
            table.applyHorizontalMerge(7, 1, 2);
            table.get(7, 3).addParagraph().appendText("职称");
            table.applyHorizontalMerge(7, 4, 5);
            table.getRows().get(7).setHeight(30f);

            table.get(8, 0).addParagraph().appendText("填报日期：");
            table.applyHorizontalMerge(8, 0, 5);
            table.getRows().get(8).setHeight(30f);


            table.get(9, 0).addParagraph().appendText("教研室负责人审阅意见：");
            table.applyHorizontalMerge(9, 0, 5);
            nullRow(table, 9, 0, 5);
            table.get(9, 0).addParagraph().appendText("签名：                       年    月    日");
            table.get(9, 0).getLastParagraph().getFormat().setHorizontalAlignment(HorizontalAlignment.Right);


            byte[] Bytes;
            String fileName = "";
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            response.reset();

            if (type == 1) {
                document.saveToStream(outputStream, FileFormat.Docx);
                fileName = "课程教学小结表.docx";

            } else if (type == 2) {
                document.saveToStream(outputStream, FileFormat.PDF);
                fileName = "课程教学小结表.pdf";
            }
            Bytes = outputStream.toByteArray();
            outputStream.close();

            response.setCharacterEncoding("utf-8");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;fileName=" + new String(fileName.getBytes(), "iso-8859-1"));

//            return ResponseEntity.ok().headers(headers).body(Bytes);
            return ResponseEntity.ok().body(Bytes);
        } catch (IOException ignored) {
            return null;
        }
    }

    //确定该课程的三个报告文档的生成情况（并将其报存）
    public DataResponses updateStatus(HttpServletResponse response, int courseId, int type) {
        CourseBasicInformation info = courseBasicInformationServiceIMPL.getById(courseId);
        try {
            File directory = new File("");
            String filePath = directory.getCanonicalPath();

            String filename1 = info.getCourseName() + "-" + info.getClassName() + "-" + info.getClassroomTeacher() + "-" + info.getTermStart() + "-" + info.getTermEnd() + "-" + info.getTerm() + "课程目标达成评价分析报告.docx";
            String filename2 = info.getCourseName() + "-" + info.getClassName() + "-" + info.getClassroomTeacher() + "-" + info.getTermStart() + "-" + info.getTermEnd() + "-" + info.getTerm() + "课程试卷分析报告.docx";
            String filename3 = info.getCourseName() + "-" + info.getClassName() + "-" + info.getClassroomTeacher() + "-" + info.getTermStart() + "-" + info.getTermEnd() + "-" + info.getTerm() + "课程教学小结表.docx";

            String filePath_ = filePath + "/doc/" + info.getMajor() + "/" + info.getClassroomTeacher() + "/" + info.getCourseName();
            File fileRealPath = new File(filePath_);
            //路径不存在则创建
            if (!fileRealPath.exists()) {
                if (!fileRealPath.mkdirs()) {
                    return new DataResponses(false);
                }
            }

            ResponseEntity<byte[]> report1 = getReport(response, courseId, type);
            byte[] body1 = report1.getBody();
            FileOutputStream fileOutputStream1 = new FileOutputStream(filePath_ + "/" + filename1);
            fileOutputStream1.write(body1);

            ResponseEntity<byte[]> report2 = getReport3(response, courseId, type);
            byte[] body2 = report2.getBody();
            FileOutputStream fileOutputStream2 = new FileOutputStream(filePath_ + "/" + filename2);
            fileOutputStream2.write(body2);

            ResponseEntity<byte[]> report3 = getReport4(response, courseId, type);
            byte[] body3 = report3.getBody();
            FileOutputStream fileOutputStream3 = new FileOutputStream(filePath_ + "/" + filename3);
            fileOutputStream3.write(body3);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new DataResponses(courseBasicInformationMAPPER.updateStatus(courseId));
    }
}
