package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.CourseSyllabusInformationMAPPER;
import com.example.object.CourseSyllabusInformation;
import com.example.object.Indicators;
import com.example.service.CourseSyllabusInformationSERVICE;
import com.example.utility.DataResponses;
import com.example.utility.DocPOI.ExcelReader;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Service
public class CourseSyllabusInformationIMPL extends ServiceImpl<CourseSyllabusInformationMAPPER, CourseSyllabusInformation> implements CourseSyllabusInformationSERVICE {

    public DataResponses getAllMajorsAndVersions() {
        @Data
        class MajorsAndVersions {
            String major;
            List<String> versions;

            public MajorsAndVersions(String major, List<String> versions) {
                this.major = major;
                this.versions = versions;
            }
        }

        List<Map<String, Object>> majorsMap = listMaps(
                new QueryWrapper<CourseSyllabusInformation>().select("DISTINCT major")
        );
        List<MajorsAndVersions> majorsAndVersions = new ArrayList<>();
        //简单粗暴
        for (Map<String, Object> majorMap : majorsMap) {
            String major = StringUtils.substringBetween(
                    majorMap.values().toString(),
                    "[", "]");
            List<String> versions = new ArrayList<>();
            listMaps(new QueryWrapper<CourseSyllabusInformation>()
                    .select("DISTINCT version")
                    .lambda()
                    .eq(CourseSyllabusInformation::getMajor, major)
            ).forEach(item -> item.values().forEach(
                    v -> versions.add(v.toString().substring(0, 4)
                    )));
            majorsAndVersions.add(new MajorsAndVersions(major, versions));
        }
        return new DataResponses(true, majorsAndVersions);
    }

    /**
     * 获得培养方案课程模版文件
     *
     * @param response httpsResponse
     * @return 文件版本为 v_1.6
     */
    public ResponseEntity<byte[]> getInputEducationProgramCourseTemplate(HttpServletResponse response) {
        //写入文件
        //使用字节数组读取
        try {
            //主义文件名
            String template = "src/main/resources/static/培养方案课程录入.xlsx";
            byte[] bytes = Files.readAllBytes(Paths.get(template));

            response.reset();
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.addHeader("Content-Disposition","attachment;filename=fileName"+".xlsx");
            response.setCharacterEncoding("utf-8");

            return ResponseEntity.ok()
                    .body(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Autowired
    CourseSyllabusInformationMAPPER courseSyllabusInformationMAPPER;

    /**
     * 解析上传的Excel文档, 将解析的培养方案课程录入教学大纲
     *
     * @param file excel文档
     * @return 执行状态
     */
    public DataResponses inputEducationProgramCourse(MultipartFile file) {
        DataResponses responses = new DataResponses();
        try {
            FileInputStream courseInputStream = (FileInputStream) file.getInputStream();
            //获得数据对象
            ExcelReader reader = new ExcelReader(courseInputStream);
            Sheet publicBasic = reader.getSheet("公共基础");
            Sheet professionBasic = reader.getSheet("专业基础");
            Sheet professionCore = reader.getSheet("专业核心");
            Sheet professionFeature = reader.getSheet("专业特色");
            Sheet handsOnTeaching = reader.getSheet("实践教学");
            Sheet qualityEducation = reader.getSheet("素质教育");
            try {
                courseInputStream.close();
            } catch (Exception e) {
                System.out.println("已经关闭啦!");
            }

            //ExcelReader professionBasic = new ExcelReader(courseInputStream, "专业基础");
            //ExcelReader professionCore = new ExcelReader(courseInputStream, "专业核心");
            //ExcelReader professionFeature = new ExcelReader(courseInputStream, "专业特色");
            //ExcelReader handsOnTeaching = new ExcelReader(courseInputStream, "实践教学");
            //ExcelReader qualityEducation = new ExcelReader(courseInputStream, "素质教育");

            //获得专业和版本
            String major = String.valueOf(reader.getValue(publicBasic, 4, 3));
            String version = String.valueOf(reader.getValue(publicBasic, 5, 3)).substring(0, 4);

            List<CourseSyllabusInformation> courseList = new ArrayList<>();
            List<String> courseType = new ArrayList<>();
            courseType.add("公共基础课");
            courseType.add("专业基础课");
            courseType.add("专业核心课");
            courseType.add("专业特色课");
            courseType.add("专业集中性实践教学");
            courseType.add("专业素质教育");
            int[] parseNumOfCourseType = {0, 0, 0, 0, 0, 0};

            //读取公共基础课程
            for (int courseIndex = 9; courseIndex < reader.getNumberOfRows(publicBasic); courseIndex++) {
                courseList.add(new CourseSyllabusInformation(
                        String.valueOf(reader.getValue(publicBasic, courseIndex, 1)),
                        StringUtils.substringBefore(String.valueOf(reader.getValue(publicBasic, courseIndex, 0)), '.'),
                        major,
                        Double.parseDouble(String.valueOf(reader.getValue(publicBasic, courseIndex, 2))),
                        Integer.parseInt(StringUtils.substringBefore(String.valueOf(reader.getValue(publicBasic, courseIndex, 3)), '.')),
                        Integer.parseInt(StringUtils.substringBefore(String.valueOf(reader.getValue(publicBasic, courseIndex, 4)), '.')),
                        "必修",
                        courseType.get(0),
                        version
                ));
            }
            parseNumOfCourseType[0] = courseList.size();

            //读取专业基础课程
            for (int courseIndex = 7; courseIndex < reader.getNumberOfRows(professionBasic); courseIndex++) {
                courseList.add(new CourseSyllabusInformation(
                        String.valueOf(reader.getValue(professionBasic, courseIndex, 1)),
                        StringUtils.substringBefore(String.valueOf(reader.getValue(professionBasic, courseIndex, 0)), '.'),
                        major,
                        Double.parseDouble(String.valueOf(reader.getValue(professionBasic, courseIndex, 2))),
                        Integer.parseInt(StringUtils.substringBefore(String.valueOf(reader.getValue(professionBasic, courseIndex, 3)), '.')),
                        Integer.parseInt(StringUtils.substringBefore(String.valueOf(reader.getValue(professionBasic, courseIndex, 4)), '.')),
                        "必修",
                        courseType.get(1),
                        version
                ));
            }
            parseNumOfCourseType[1] = courseList.size() - IntStream.of(parseNumOfCourseType).sum();

            //读取专业核心课程
            for (int courseIndex = 7; courseIndex < reader.getNumberOfRows(professionCore); courseIndex++) {
                courseList.add(new CourseSyllabusInformation(
                        String.valueOf(reader.getValue(professionCore, courseIndex, 1)),
                        StringUtils.substringBefore(String.valueOf(reader.getValue(professionCore, courseIndex, 0)), '.'),
                        major,
                        Double.parseDouble(String.valueOf(reader.getValue(professionCore, courseIndex, 2))),
                        Integer.parseInt(StringUtils.substringBefore(String.valueOf(reader.getValue(professionCore, courseIndex, 3)), '.')),
                        Integer.parseInt(StringUtils.substringBefore(String.valueOf(reader.getValue(professionCore, courseIndex, 4)), '.')),
                        "必修",
                        courseType.get(2),
                        version
                ));
            }
            parseNumOfCourseType[2] = courseList.size() - IntStream.of(parseNumOfCourseType).sum();

            //读取专业特色课程
            for (int courseIndex = 7; courseIndex < reader.getNumberOfRows(professionFeature); courseIndex++) {
                courseList.add(new CourseSyllabusInformation(
                        String.valueOf(reader.getValue(professionFeature, courseIndex, 1)),
                        StringUtils.substringBefore(String.valueOf(reader.getValue(professionFeature, courseIndex, 0)), '.'),
                        major,
                        Double.parseDouble(String.valueOf(reader.getValue(professionFeature, courseIndex, 2))),
                        Integer.parseInt(StringUtils.substringBefore(String.valueOf(reader.getValue(professionFeature, courseIndex, 3)), '.')),
                        Integer.parseInt(StringUtils.substringBefore(String.valueOf(reader.getValue(professionFeature, courseIndex, 4)), '.')),
                        "选修",
                        courseType.get(3),
                        version
                ));
            }
            parseNumOfCourseType[3] = courseList.size() - IntStream.of(parseNumOfCourseType).sum();

            //读取集中性实践教学课程
            for (int courseIndex = 7; courseIndex < reader.getNumberOfRows(handsOnTeaching); courseIndex++) {
                courseList.add(new CourseSyllabusInformation(
                        String.valueOf(reader.getValue(handsOnTeaching, courseIndex, 1)),
                        StringUtils.substringBefore(String.valueOf(reader.getValue(handsOnTeaching, courseIndex, 0)), '.'),
                        major,
                        Double.parseDouble(String.valueOf(reader.getValue(handsOnTeaching, courseIndex, 2))),
                        0,
                        Integer.parseInt(StringUtils.substringBefore(String.valueOf(reader.getValue(handsOnTeaching, courseIndex, 3)), '.')),
                        "必修",
                        courseType.get(4),
                        version
                ));
            }
            parseNumOfCourseType[4] = courseList.size() - IntStream.of(parseNumOfCourseType).sum();

            //读取专业素质教育课程
            for (int courseIndex = 7; courseIndex < reader.getNumberOfRows(qualityEducation); courseIndex++) {
                courseList.add(new CourseSyllabusInformation(
                        String.valueOf(reader.getValue(qualityEducation, courseIndex, 1)),
                        StringUtils.substringBefore(String.valueOf(reader.getValue(qualityEducation, courseIndex, 0)), '.'),
                        major,
                        Double.parseDouble(String.valueOf(reader.getValue(qualityEducation, courseIndex, 2))),
                        Integer.parseInt(StringUtils.substringBefore(String.valueOf(reader.getValue(qualityEducation, courseIndex, 3)), '.')),
                        0,
                        "必修",
                        courseType.get(5),
                        version
                ));
            }
            parseNumOfCourseType[5] = courseList.size() - IntStream.of(parseNumOfCourseType).sum();

            responses.setMessage(major + " 专业" + version + " 版培养方案课程数据录入成功!");
            responses.setFlag(saveBatch(courseList));
            responses.setData("共解析 " + courseList.size() + " 条课程数据, 具体如下");
            responses.setData1(courseType);
            responses.setData2(parseNumOfCourseType);
        } catch (Exception e) {
            e.printStackTrace();
            responses.setFlag(false);
            responses.setMessage("课程信息录入失败!\n请检查上传的数据表格是否填写完整!(单元格不能有空)");
        }
        return responses;
    }
}
