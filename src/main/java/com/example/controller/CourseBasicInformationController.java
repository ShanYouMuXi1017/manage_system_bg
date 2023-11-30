package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.mapper.CourseSyllabusInformationMAPPER;
import com.example.mapper.CourseTargetMAPPER;
import com.example.mapper.IndicatorsMAPPER;
import com.example.mapper.courseSurvey.CourseAttainmentSurveyMAPPER;
import com.example.object.CourseBasicInformation;
import com.example.object.CourseSyllabusInformation;
import com.example.object.CourseTarget;
import com.example.object.Indicators;
import com.example.service.impl.CourseBasicInformationServiceIMPL;
import com.example.service.impl.CourseSyllabusInformationIMPL;
import com.example.service.impl.IndicatorOutlineSERVICEIMPL;
import com.example.service.impl.IndicatorsServiceIMPL;
import com.example.utility.DataResponses;
import com.example.utility.export.export;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

@CrossOrigin(origins = "*")
@Api(tags = "课程信息")
@RestController
@RequestMapping("/courseInfo")
public class CourseBasicInformationController {

    @ApiOperation("检查登录接口")
    @PostMapping("/checkSubmit")
    public DataResponses checkSubmit() {
        return new DataResponses(true);
    }

    /*
        课程基本信息相关接口
     */
    //课程基本信息
    @Autowired
    private CourseBasicInformationServiceIMPL courseBasicInformationService;

    @ApiOperation("查询全部")
    @GetMapping
    public DataResponses getAll() {
        return new DataResponses(true, courseBasicInformationService.list());
    }

    @ApiOperation("按当前用户查询")
    @GetMapping("/currentUser/{currentUserId}")
    public DataResponses getByCurrentUser(@PathVariable int currentUserId) {
        QueryWrapper<CourseBasicInformation> QueryWrapper = new QueryWrapper<>();
        QueryWrapper.eq("teacher_id", currentUserId);
        QueryWrapper.orderByDesc("term_start");
        QueryWrapper.orderByAsc("term");
        return new DataResponses(true, courseBasicInformationService.list(QueryWrapper));
    }

    @ApiOperation("当前用户筛选")
    @PostMapping("/currentUser/{currentUserId}")
    public DataResponses searchByCurrentUser(@PathVariable int currentUserId, @RequestBody CourseBasicInformation searchTable) {
        QueryWrapper<CourseBasicInformation> QueryWrapper = new QueryWrapper<>();
        QueryWrapper.eq("teacher_id", currentUserId);
        QueryWrapper.orderByDesc("term_start");
        QueryWrapper.orderByAsc("term");
        if (searchTable.getCourseName() != null) {
            QueryWrapper.like("course_name", searchTable.getCourseName());
        }
        if (searchTable.getClassName() != null) {
            QueryWrapper.like("class_name", searchTable.getClassName());
        }
        if (searchTable.getTermStart() != null) {
            QueryWrapper.like("term_start", searchTable.getTermStart());
        }
        if (searchTable.getTermEnd() != null) {
            QueryWrapper.like("term_end", searchTable.getTermEnd());
        }
        if (searchTable.getTerm() != 0) {
            QueryWrapper.like("term", searchTable.getTerm());
        }
        return new DataResponses(true, courseBasicInformationService.list(QueryWrapper));
    }

    @ApiOperation("按id查询")
    @GetMapping("/{id}")
    public DataResponses getById(@PathVariable int id) {
        return new DataResponses(true, courseBasicInformationService.getById(id));
    }

    @ApiOperation("按id修改")
    @PutMapping()
    public DataResponses UpdateById(@RequestBody CourseBasicInformation data) {
        return new DataResponses(courseBasicInformationService.updateById(data));
    }

    @ApiOperation("导出课程基本信息")
    @GetMapping("/export/{id}")
    public void exportExcel(HttpServletResponse response, @PathVariable int id) throws IOException {

        CourseBasicInformation information = courseBasicInformationService.getById(id);
        //导出文件的方法统一写入到export类中
        export.ExportCourseBasicInformationExcel(response, information);
    }

    @ApiOperation("添加")
    @PostMapping
    public DataResponses write(@RequestBody CourseBasicInformation pages) {
        return new DataResponses(courseBasicInformationService.save(pages));
    }

    @ApiOperation("删除")
    @DeleteMapping
    public DataResponses delete(@RequestBody CourseBasicInformation pages) {
        return new DataResponses(courseBasicInformationService.removeById(pages));
    }

/*
    课程目标相关接口
 */

    //课程目标
    @Autowired
    private CourseTargetMAPPER courseTarget;

    //课程目标调查问卷
    @Autowired
    private CourseAttainmentSurveyMAPPER courseAttainmentSurveyMAPPER;

    @ApiOperation("获取该课程所有课程目标")
    @GetMapping("/courseTarget/{courseId}")
    public DataResponses getCourseTarget(@PathVariable int courseId) {
        QueryWrapper<CourseTarget> QueryWrapper = new QueryWrapper<>();
        QueryWrapper.eq("course_id", courseId);
        return new DataResponses(true, courseTarget.selectList(QueryWrapper));
    }

    @ApiOperation("添加该课程课程目标")
    @PostMapping("/courseTarget")
    public DataResponses addCourseTarget(@RequestBody CourseTarget Data) {
//        QueryWrapper<CourseTarget> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("course_id", Data.getCourseId());
//        queryWrapper.eq("target_name", Data.getTargetName());
//        CourseTarget target = courseTarget.selectOne(queryWrapper);
//
//        CourseAttainmentSurvey courseAttainmentSurvey = new CourseAttainmentSurvey();
//        courseAttainmentSurvey.setCourseTargetId(target.getId());
//        courseAttainmentSurveyMAPPER.insert(courseAttainmentSurvey);
        return new DataResponses(true, courseTarget.insert(Data),String.valueOf(Data.getId()));
    }

    @ApiOperation("修改课程目标")
    @PutMapping("/courseTarget")
    public DataResponses modifyCourseTarget(@RequestBody CourseTarget Data) {
        return new DataResponses(true, courseTarget.updateById(Data));
    }

    @ApiOperation("删除课程目标")
    @DeleteMapping("/courseTarget")
    public DataResponses DeleteCourseTarget(@RequestBody CourseTarget Data) {
        return new DataResponses(true, courseTarget.deleteById(Data));
    }

    /*
        指标点相关接口
     */

    @Autowired
    IndicatorOutlineSERVICEIMPL indicatorOutlineSERVICEIMPL;

    @ApiOperation("查询指标点概要")
    @GetMapping("/indicatorsOutline")
    public DataResponses getIndicatorOutline() {
        return new DataResponses(true, indicatorOutlineSERVICEIMPL.list());
    }

    @Autowired
    private IndicatorsMAPPER indicators;
    @Autowired
    private IndicatorsServiceIMPL indicatorsServiceIMPL;

    @ApiOperation("查询全部指标点")
    @GetMapping("/indicators")
    public DataResponses getAllIndicators() {
        return new DataResponses(true, indicatorsServiceIMPL.list());
    }

    @ApiOperation("添加指标点")
    @PostMapping("/saveIndicator")
    public DataResponses insertIndicators(@RequestBody Indicators item) {
        return new DataResponses(true, indicatorsServiceIMPL.save(item));
    }

    @ApiOperation("查询所有指标点所有专业和版本")
    @GetMapping("/indicatorMajorsAndVersions")
    public DataResponses getAllIndicatorMajors() {
        QueryWrapper majorQueryWrapper = new QueryWrapper<>();
        majorQueryWrapper.select("DISTINCT major");
        QueryWrapper versionQueryWrapper = new QueryWrapper<>();
        versionQueryWrapper.select("DISTINCT version");
        List majors = indicatorsServiceIMPL.listMaps(majorQueryWrapper);
        List versions = indicatorsServiceIMPL.listMaps(versionQueryWrapper);
        return new DataResponses(true, new List[]{majors, versions});
    }

    @ApiOperation("查询专业指标点")
    @PostMapping("/indicators")
    public DataResponses getMajorIndicators(@RequestBody HashMap<String, String> major) {
        return new DataResponses(true, indicators.getMajorIndicators(major.get("major")));
    }

    @ApiOperation("删除指标点")
    @DeleteMapping("/indicators")
    public DataResponses removeIndicators(@RequestBody Indicators item) {
        return new DataResponses(indicators.deleteById(item));
    }

    @ApiOperation("修改指标点")
    @PutMapping("/indicators")
    public DataResponses PutIndicators(@RequestBody Indicators item) {
        return new DataResponses(indicators.updateById(item));
    }

    @ApiOperation("按专业和版本号查询指标点")
    @PostMapping("/majorVersionIndicators")
    public DataResponses getMajorVersionIndicators(@RequestBody HashMap<String, String> majorAndVersion) {
        QueryWrapper<Indicators> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("major", majorAndVersion.get("major"));
        queryWrapper.eq(!(majorAndVersion.get("version")==null), "version", majorAndVersion.get("version"));
        return new DataResponses(true, indicatorsServiceIMPL.list(queryWrapper));
    }

    @Autowired
    CourseSyllabusInformationIMPL courseSyllabusInformationIMPL;
    @ApiOperation("指标点课程")
    @PostMapping("/indicatorCourse")
    public DataResponses getMajorIndicatorCourse(@RequestBody HashMap<String, Object> major) {
        return new DataResponses(true, courseSyllabusInformationIMPL.listByMap(major));
    }

    @ApiOperation("指标点PDF")
    @GetMapping("/indicatorsPDF/{major}/{version}")
    public ResponseEntity<byte[]> IndicatorsPDF(@PathVariable String major, @PathVariable String version) {
        return indicatorsServiceIMPL.IndicatorsPDF(major, version);
    }

    @ApiOperation("指标点Word")
    @GetMapping("/indicatorsWord/{major}/{version}")
    public ResponseEntity<byte[]> IndicatorsWord(HttpServletResponse response, @PathVariable String major, @PathVariable String version) {
        return indicatorsServiceIMPL.IndicatorsWord(response, major, version);
    }

    /*
        教学大纲相关接口
     */
    //课程教学大纲信息
    @Autowired
    private CourseSyllabusInformationMAPPER courseSyllabusInformationMAPPER;

    @ApiOperation("教学大纲PDF上传")
    @PostMapping("/syllabus")
    public DataResponses teachingPDF(@RequestParam("file") MultipartFile file, @RequestParam("id") int id) {
        DataResponses res = new DataResponses(false, "上传失败");
        try {
            String contentType = file.getContentType();
            if (contentType == null) {
                return res;
            } else if (!contentType.equals("application/pdf")) {
                res.setMessage("只能上传pdf文件");
                return res;
            }
            File directory = new File("");
            String filePath = directory.getCanonicalPath();

            CourseSyllabusInformation courseSyllabusInformation = courseSyllabusInformationMAPPER.selectById(id);

            String filename = courseSyllabusInformation.getCourseName() + ".pdf";
            String filePath_ = filePath + "/pdf/syllabus/" + courseSyllabusInformation.getMajor() + "/" + courseSyllabusInformation.getCourseType();
            File fileRealPath = new File(filePath_);
            //路径不存在则创建
            if (!fileRealPath.exists()) {
                if (!fileRealPath.mkdirs()) {
                    return res;
                }
            }
            File result = new File(filePath_ + "/" + filename);
            file.transferTo(result);
            courseSyllabusInformation.setFileAddress(filePath_ + "/" + filename);
            res = new DataResponses(courseSyllabusInformationMAPPER.updateById(courseSyllabusInformation) == 1, filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    @ApiOperation("培养方案PDF上传")
    @PostMapping("/educationProgramPDF")
    public DataResponses educationProgramPDF(@RequestParam("file") MultipartFile file, @RequestParam String major, @RequestParam String type) {
        DataResponses res = new DataResponses(false, "上传失败");
        try {
            String contentType = file.getContentType();
            if (contentType == null) {
                return res;
            } else if (!contentType.equals("application/pdf")) {
                res.setMessage("只能上传pdf文件");
                return res;
            }
            File directory = new File("");//参数为空
            String filePath = directory.getCanonicalPath();

            String filename = major + ".pdf";
            String filePath_ = filePath + "/pdf/" + type;
            File fileRealPath = new File(filePath_);
            //路径不存在则创建
            if (!fileRealPath.exists()) {
                if (!fileRealPath.mkdirs()) {
                    return res;
                }
            }
            File result = new File(filePath_ + "/" + filename);
            file.transferTo(result);
            res = new DataResponses(true, filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    @ApiOperation("培养方案PDF查看")
    @GetMapping("/file/{type}/{filename:.*\\.pdf}")
    public ResponseEntity<byte[]> getFile(@PathVariable String type, @PathVariable String filename) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        File directory = new File("");//参数为空
        String filePath = directory.getCanonicalPath();

        Path path = Paths.get(filePath + "/pdf/" + type + '/' + filename);
        File file = path.toFile();
        if (!file.exists()) {
            headers.setContentType(MediaType.APPLICATION_JSON);
            return new ResponseEntity<>(null, headers, HttpStatus.OK);
        }
        // 获取文件的字节数组
        byte[] bytes = Files.readAllBytes(path);

        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentLength(bytes.length);

        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    @ApiOperation("教学大纲PDF查看")
    @GetMapping("/file/{courseId}/syllabus")
    public ResponseEntity<byte[]> getSyllabusPDF(@PathVariable String courseId) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        File directory = new File("");//参数为空
        String filePath = directory.getCanonicalPath();

        CourseSyllabusInformation courseSyllabusInformation = courseSyllabusInformationMAPPER.selectById(courseId);

        Path path = Paths.get(filePath + "/pdf/syllabus/" + courseSyllabusInformation.getMajor() + '/' + courseSyllabusInformation.getCourseType() + '/' + courseSyllabusInformation.getCourseName() + ".pdf");
        File file = path.toFile();
        if (!file.exists()) {
            headers.setContentType(MediaType.APPLICATION_JSON);
            return new ResponseEntity<>(null, headers, HttpStatus.OK);
        }
        // 获取文件的字节数组
        byte[] bytes = Files.readAllBytes(path);

        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentLength(bytes.length);

        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    @ApiOperation("教学大纲PDF删除")
    @DeleteMapping("/file/syllabus/{major}/{type}/{fileName}")
    public DataResponses deleteSyllabusPDF(@PathVariable String major, @PathVariable String type, @PathVariable String fileName) throws IOException {
        File directory = new File("");//参数为空
        String filePath = directory.getCanonicalPath();

        Path path = Paths.get(filePath + "/pdf/syllabus/" + major + '/' + type + '/' + fileName);
        File file = path.toFile();
        if (!file.delete()) {
            return new DataResponses(false, "删除失败");
        }
        return new DataResponses(true, "删除成功");
    }

    @ApiOperation("获取教学大纲pdf列表")
    @PostMapping("/syllabusList")
    public DataResponses getPdfList(@RequestBody HashMap<String, String> info) {
        DataResponses res = new DataResponses();
        try {
            //当前项目路径
            String filePath = new File("").getCanonicalPath();

            String filePath_ = filePath + "/pdf/syllabus/" + info.get("major") + "/" + info.get("type");
            File fileRealPath = new File(filePath_);

            QueryWrapper<CourseSyllabusInformation> queryWrapper = new QueryWrapper<>();
            queryWrapper.like("major", info.get("major"));
            queryWrapper.like("course_type", info.get("type"));

            return new DataResponses(true, courseSyllabusInformationMAPPER.selectList(queryWrapper));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    @ApiOperation("获取当前专业所有课程教学大纲")
    @GetMapping("/{major}/getAllCourseByMajor")
    public DataResponses getAllCourseByMajor(@PathVariable String major) {
        QueryWrapper<CourseSyllabusInformation> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("major", major);
        queryWrapper.orderByAsc("course_name");
        return new DataResponses(true, courseSyllabusInformationMAPPER.selectList(queryWrapper));
    }

    @ApiOperation("选择课程后(按照教学大纲)自动填充相关信息")
    @GetMapping("/{courseId}/autoGenerate")
    public DataResponses autoGenerate(@PathVariable int courseId) {
        return new DataResponses(true, courseSyllabusInformationMAPPER.selectById(courseId));
    }
    @ApiOperation("添加课程教学接口")
    @PutMapping("/syllabus")
    public DataResponses insert(@RequestBody CourseSyllabusInformation item) {
        return new DataResponses(true, courseSyllabusInformationMAPPER.insert(item));
    }

}