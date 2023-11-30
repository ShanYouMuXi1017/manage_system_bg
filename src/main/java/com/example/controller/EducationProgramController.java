package com.example.controller;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.mapper.EducationProgramMAPPER;
import com.example.object.EducationProgram;
import com.example.service.EducationProgramSERVICE;
import com.example.utility.DataResponses;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@Api(tags = "培养方案")
@RestController
@RequestMapping("/courseInfo")
public class EducationProgramController {

    @Resource
    private EducationProgramMAPPER educationProgramMAPPER;

    @ApiOperation("添加培养方案记录")
    @PostMapping("/educationPrograms")
    public DataResponses addEducationProgram(@RequestBody EducationProgram educationProgram) {
        return new DataResponses(educationProgramMAPPER.insert(educationProgram));
    }

    @ApiOperation("查询所插入的培养方案是否已存在")
    @GetMapping("/isExitForMajorVersion/{major}/{majorVersion}")
    public DataResponses isExitForMajorVersion(@PathVariable String major,@PathVariable String majorVersion){
/*        QueryWrapper<EducationProgram> QueryWrapper = new QueryWrapper<>();
        QueryWrapper.eq("major",major).eq("major_version",majorVersion);
        return new DataResponses(true,educationProgramMAPPER.exists(QueryWrapper));*/

        QueryWrapper<EducationProgram> queryWrapper = new QueryWrapper<>();
        Map<String, Object> queryParamsMap = new HashMap<>();
        queryParamsMap.put("major", major);
        queryParamsMap.put("major_version", majorVersion);
        queryWrapper.allEq(queryParamsMap);
        return new DataResponses(true,educationProgramMAPPER.exists(queryWrapper));
    }


    @ApiOperation("修改培养方案记录")
    @PutMapping("/educationPrograms/update")
    public DataResponses updateEducationProgram(@RequestBody EducationProgram educationProgram) {
        return new DataResponses(educationProgramMAPPER.updateById(educationProgram));
    }

    @ApiOperation("删除培养方案记录")
    @DeleteMapping("/deleteEducationProgram/{id}")
    public DataResponses deleteEducationProgram(@PathVariable("id") Integer id) throws IOException {
        EducationProgram educationProgram = educationProgramMAPPER.selectById(id);
        String filePath = new File("").getCanonicalPath();

        String filename = filePath + "/pdf/educationProgram/" + educationProgram.getMajor()+"/" +educationProgram.getMajor() + "-" + educationProgram.getMajorVersion() + "版.pdf";

        File fileRealPath = new File(filename);
        if (fileRealPath.exists()) {
            if (!fileRealPath.delete()) {
                return new DataResponses(false,"文件删除失败");
            }

        }
        educationProgram.setIsLoad(0);
        return new DataResponses(educationProgramMAPPER.deleteById(id));
    }

    @ApiOperation("根据id查找方案记录")
    @GetMapping("/findById/{id}")
    public DataResponses findById(@PathVariable Integer id) {
        return new DataResponses(true, educationProgramMAPPER.selectById(id));
    }

    @ApiOperation("查询所有方案记录")
    @GetMapping("/findAll")
    public DataResponses findAll() {
        return new DataResponses(true,educationProgramMAPPER.selectList(null));
    }

    @ApiOperation("根据专业名称查询")
    @GetMapping("/{major}/findByMajor")
    public DataResponses findByMajor(@PathVariable String major) {
        QueryWrapper<EducationProgram> QueryWrapper = new QueryWrapper<>();
        QueryWrapper.eq("major", major).orderByDesc("major_version");
        return new DataResponses(true, educationProgramMAPPER.selectList(QueryWrapper));
    }

/*    // TODO
    @ApiOperation("查询表中所有的专业名称")
    @GetMapping("/findMajors")
    public DataResponses findMajors(){
        QueryWrapper<EducationProgram> QueryWrapper = new QueryWrapper<>();
        QueryWrapper.select("DISTINCT major");
        List list = educationProgramMAPPER.selectList(QueryWrapper);
        return new DataResponses(true,list);
    }*/


    @ApiOperation("上传对应专业的pdf方案")
    @PostMapping("/uploadPDF")
    public DataResponses uploadPDF(@RequestParam("file") MultipartFile file, @RequestParam("id") Integer id) {

        String contentType = file.getContentType();
        if (contentType == null) {
            return new DataResponses(false,"文件不能为空");
        }
        if (!contentType.equals("application/pdf")) {
            return new DataResponses(false,"文件类型不匹配");
        }
        EducationProgram educationProgram = educationProgramMAPPER.selectById(id);

        try {
            String filePath = new File("").getCanonicalPath();

            String filename = educationProgram.getMajor() + "-" + educationProgram.getMajorVersion() + "版.pdf";

            String filePath_ = filePath + "/pdf/educationProgram/" + educationProgram.getMajor();
            File fileRealPath = new File(filePath_);
            if (!fileRealPath.exists()) {
                if (!fileRealPath.mkdirs()) {
                    return new DataResponses(false,"文件夹创建失败");
                }
            }

            File result = new File(filePath_ + "/" + filename);
            file.transferTo(result);
            educationProgram.setIsLoad(1);
            educationProgramMAPPER.updateById(educationProgram);
            return new DataResponses(educationProgramMAPPER.updateById(educationProgram) == 1,filename);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @ApiOperation("展示对应的方案文件")
    @GetMapping("/showPDF/{major}/{majorVersion}")
    public ResponseEntity<byte[]> showPDF(@PathVariable String major, @PathVariable String majorVersion) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        String filePath = new File("").getCanonicalPath();

        String filename2 = major + "-" + majorVersion + "版.pdf";

        Path path = Paths.get(filePath + "/pdf/educationProgram/" + major + "/" + filename2);
        File file = path.toFile();
        if (!file.exists()) {
            headers.setContentType(MediaType.APPLICATION_JSON);
            return new ResponseEntity<>(null, headers, HttpStatus.OK);
        }
        byte[] bytes = Files.readAllBytes(path);
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentLength(bytes.length);
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    @ApiOperation("对应方案下载")
    @GetMapping("/download/{major}/{majorVersion}")
    public DataResponses downloadPDF(@PathVariable String major,@PathVariable String majorVersion, HttpServletResponse response) throws IOException {

        response.addHeader("Content-Disposition","attachment;filename="+ URLEncoder.encode(major+"-"+majorVersion+"版.pdf","UTF-8"));

        String filePath = new File("").getCanonicalPath()+"/pdf/educationProgram/"+major+"/"+major+"-"+majorVersion+"版.pdf";
        if(!FileUtil.exist(filePath)){
            return new DataResponses(false,"文件下载失败");
        }
        byte[] bytes = FileUtil.readBytes(filePath);
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(bytes);
        outputStream.flush();
        outputStream.close();
        return new DataResponses(true,"文件下载成功");
    }
}
