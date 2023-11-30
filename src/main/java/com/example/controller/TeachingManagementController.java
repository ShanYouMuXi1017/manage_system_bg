package com.example.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.object.CourseBasicInformation;
import com.example.service.impl.CourseBasicInformationServiceIMPL;
import com.example.utility.DataResponses;

import com.sini.com.spire.doc.Document;
import com.sini.com.spire.doc.FileFormat;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


@CrossOrigin(origins = "*")
@Api(tags = "分析报告")
@RestController
@RequestMapping("/manager")
public class TeachingManagementController {
    @Autowired
    private CourseBasicInformationServiceIMPL courseBasicInformationServiceIMPL;


    @ApiOperation("获取当前专业的所有课程")
    @PostMapping("/getCourseByMajor")
    public DataResponses getCourseByMajor(@RequestBody HashMap<String, String> major) {
        QueryWrapper<CourseBasicInformation> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("major", major.get("major"));
        queryWrapper.orderByDesc("term_start");
        queryWrapper.orderByAsc("term");
        return new DataResponses(true, courseBasicInformationServiceIMPL.list(queryWrapper));
    }

    @ApiOperation("查看相关文件")
    @GetMapping("/{courseId}/{type}/file")
    public ResponseEntity<byte[]> getPDF(@PathVariable String courseId, @PathVariable int type) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        File directory = new File("");
        String filePath = directory.getCanonicalPath();

        CourseBasicInformation info = courseBasicInformationServiceIMPL.getById(courseId);

        String s = "/doc/" + info.getMajor() + "/" + info.getClassroomTeacher() + "/" + info.getCourseName() + "/";

        String filename1 = s + info.getCourseName() + "-" + info.getClassName() + "-" + info.getClassroomTeacher() + "-" + info.getTermStart() + "-" + info.getTermEnd() + "-" + info.getTerm() + "课程目标达成评价分析报告.docx";
        String filename2 = s + info.getCourseName() + "-" + info.getClassName() + "-" + info.getClassroomTeacher() + "-" + info.getTermStart() + "-" + info.getTermEnd() + "-" + info.getTerm() + "课程试卷分析报告.docx";
        String filename3 = s + info.getCourseName() + "-" + info.getClassName() + "-" + info.getClassroomTeacher() + "-" + info.getTermStart() + "-" + info.getTermEnd() + "-" + info.getTerm() + "课程教学小结表.docx";

        String path;
        switch (type) {
            case 1:
                path = filePath + filename1;
                break;
            case 2:
                path = filePath + filename2;
                break;
            case 3:
                path = filePath + filename3;
                break;
            default:
                path = filePath;
                break;
        }
        Path realPath = Paths.get(path);
        File file = realPath.toFile();
        if (!file.exists()) {
            headers.setContentType(MediaType.APPLICATION_JSON);
            return new ResponseEntity<>(null, headers, HttpStatus.OK);
        }

        Document doc = new Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        doc.loadFromFile(path);
        doc.saveToStream(outputStream, FileFormat.PDF);
        byte[] bytes = outputStream.toByteArray();

        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentLength(bytes.length);

        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    @ApiOperation("下载文件压缩包")
    @GetMapping("/{courseId}/downloadZip")
    public ResponseEntity<byte[]> downloadZip(@PathVariable String courseId) {

        CourseBasicInformation info = courseBasicInformationServiceIMPL.getById(courseId);

        String zipFileName = info.getCourseName() + "-" + info.getClassName() + "-" + info.getClassroomTeacher() + "-" + info.getTermStart() + "-" + info.getTermEnd() + "-" + info.getTerm() + ".zip";

        try {
            File directory = new File("");
            String filePath = directory.getCanonicalPath();

            String sourceFolder = "/doc/" + info.getMajor() + "/" + info.getClassroomTeacher() + "/" + info.getCourseName() + "/";
            String filename = info.getCourseName() + "-" + info.getClassName() + "-" + info.getClassroomTeacher() + "-" + info.getTermStart() + "-" + info.getTermEnd() + "-" + info.getTerm();

            String s1 = "课程目标达成评价分析报告.docx";
            String s2 = "课程试卷分析报告.docx";
            String s3 = "课程教学小结表.docx";

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);

            File sourceDir1 = new File(filePath + sourceFolder + filename + s1);
            File sourceDir2 = new File(filePath + sourceFolder + filename + s2);
            File sourceDir3 = new File(filePath + sourceFolder + filename + s3);
            addFilesToZip(sourceDir1, sourceDir1.getName(), zipOutputStream);
            addFilesToZip(sourceDir2, sourceDir2.getName(), zipOutputStream);
            addFilesToZip(sourceDir3, sourceDir3.getName(), zipOutputStream);

            zipOutputStream.close();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", new String(zipFileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));

            return new ResponseEntity<>(byteArrayOutputStream.toByteArray(), headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation("批量下载")
    @GetMapping("/downloadZip")
    public ResponseEntity<byte[]> batchDownLoad(@RequestParam(name = "id") String courseIdGroup) {
        String[] ids = courseIdGroup.split(",");

        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ZipOutputStream zipStream = new ZipOutputStream(byteStream);
        String FileName = "批量导出.zip";

        try {

            File directory = new File("");
            String filePath = directory.getCanonicalPath();

            for (String s : ids) {
                CourseBasicInformation info = courseBasicInformationServiceIMPL.getById(s);
                String zipFileName = info.getCourseName() + "-" + info.getClassName() + "-" + info.getClassroomTeacher() + "-" + info.getTermStart() + "-" + info.getTermEnd() + "-" + info.getTerm() + ".zip";

                String sourceFolder = "/doc/" + info.getMajor() + "/" + info.getClassroomTeacher() + "/" + info.getCourseName() + "/";
                String filename = info.getCourseName() + "-" + info.getClassName() + "-" + info.getClassroomTeacher() + "-" + info.getTermStart() + "-" + info.getTermEnd() + "-" + info.getTerm();

                String s1 = "课程目标达成评价分析报告.docx";
                String s2 = "课程试卷分析报告.docx";
                String s3 = "课程教学小结表.docx";

                ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFileName));

                File sourceDir1 = new File(filePath + sourceFolder + filename + s1);
                File sourceDir2 = new File(filePath + sourceFolder + filename + s2);
                File sourceDir3 = new File(filePath + sourceFolder + filename + s3);
                addFilesToZip(sourceDir1, sourceDir1.getName(), zipOutputStream);
                addFilesToZip(sourceDir2, sourceDir2.getName(), zipOutputStream);
                addFilesToZip(sourceDir3, sourceDir3.getName(), zipOutputStream);

                zipOutputStream.flush();
                zipOutputStream.close();

                File zip = new File(zipFileName);
                addFilesToZip(zip, zip.getName(), zipStream);
                zip.delete();
            }

            zipStream.flush();
            zipStream.close();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", new String(FileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));

            return new ResponseEntity<>(byteStream.toByteArray(), headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //添加文件到压缩包中
    private void addFilesToZip(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        ZipEntry entry = new ZipEntry(fileName);
        zipOut.putNextEntry(entry);

        if (fileToZip.isFile()) {
            FileInputStream fis = new FileInputStream(fileToZip);
            BufferedInputStream bis = new BufferedInputStream(fis);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                zipOut.write(buffer, 0, bytesRead);
            }
            bis.close();
        } else if (fileToZip.isDirectory()) {
            File[] children = fileToZip.listFiles();
            if (children != null) {
                for (File childFile : children) {
                    addFilesToZip(childFile, fileName + "/" + childFile.getName(), zipOut);
                }
            }
        }

        zipOut.closeEntry();
    }
}
