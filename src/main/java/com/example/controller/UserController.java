package com.example.controller;

import cn.dev33.satoken.stp.StpUtil;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.object.College;

import com.example.object.LoginDTO;
import com.example.object.User;
import com.example.service.impl.UserServiceIMPL;
import com.example.utility.DataResponses;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "用户登录")
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired

    private UserServiceIMPL userService;


    @Autowired

    private UserServiceIMPL userServiceIMPL;

    // 会话登录接口
    @PostMapping("/doLogin")
    public DataResponses doLogin(@RequestBody LoginDTO user) {
        return userServiceIMPL.login(user);
    }

    // 查询登录信息 登录信息可以不存在前端，每次需通过后端验证获取
    @GetMapping("/info")
    public DataResponses isLogin() {
        Map<String, Object> map = new HashMap<>();
        String loginId = StpUtil.getLoginIdAsString();
        Object o = StpUtil.getSession().get(loginId);
        map.put("info", o);
        if (loginId.startsWith("admin_")) {
            map.put("identity", "0");
        } else {
            map.put("identity", "1");
        }
        return new DataResponses(true, map);
    }

    /**
     * 退出
     *
     * @return DataResponses
     */
    @GetMapping("/logout")
    public DataResponses logout() {
        StpUtil.logout();
        return new DataResponses(true);
    }

    /**
     * 多角色账号选择登录的角色
     *
     * @param role 0    普通教师
     *             1    系主任
     *             2    学院
     * @return DataResponses
     */
    @PostMapping("/choiceRole")
    public DataResponses choiceRole(@RequestBody Map<String, Integer> role) {
        User user = (User) StpUtil.getSession().get(StpUtil.getLoginIdAsString());
        user.setIsAdmin(role.get("role"));
        StpUtil.getSession().set(String.valueOf(user.getId()), user);
        return new DataResponses(true, user);
    }

    @ApiOperation("登录接口")
    @PostMapping
    public DataResponses submit(@RequestBody User user, HttpServletResponse response) {
        return userServiceIMPL.loginCheck(user, response);
    }

    @ApiOperation("按id修改")
    @PutMapping("/updateUser")
    public DataResponses updateUser(@RequestBody User data) {
        return new DataResponses(true,userServiceIMPL.updateById(data));
    }

    @ApiOperation("添加")
    @PostMapping("/addUser")
    public DataResponses addUser(@RequestBody User user) {
        return new DataResponses(true, userServiceIMPL.save(user),user.getName());
    }

    @ApiOperation("重置密码")
    @PutMapping("/resetPassword")
    public DataResponses resetPassword(@RequestBody User data) {
        data.setPassword("000000");
        return new DataResponses(true,userService.updateById(data));
    }


    @ApiOperation("删除")
    @DeleteMapping("/deleteUser")
    public DataResponses deleteUser(@RequestBody User user) {
        return new DataResponses(true,userServiceIMPL.removeById(user.getId()));
    }

    @ApiOperation("查询全部")
    @GetMapping
    public DataResponses getAll() {

        List<User> data= userService.list() ;
        List<College> data2= userService.userPrCollegeList() ;
        List<College> data3= userService.userDerList() ;
        return new DataResponses(true, data,data2,data3);

    }

    /**
     * 导入教师信息
     */
    @ApiOperation("导入教师信息表格")
    @PostMapping("/userInfo")
    public DataResponses inputUserInfo(@RequestParam("file") MultipartFile file) {
        return userServiceIMPL.inputUserInfo(file);
    }

    /*
    *教学大纲管理页面的课程负责人查询
    */
    @ApiOperation("负责人查询")
    @GetMapping("/searchTeacher")
    public DataResponses searchTeacher() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        queryWrapper.select("teacher_name");
        queryWrapper.select("teacher_name").notLike("teacher_name","管理员").eq("is_admin",0);
        return new DataResponses(true, userServiceIMPL.list(queryWrapper));
    }
 /**
     * 导出用户导入模板
     */
    @ApiOperation("导出用户导入模板")
    @GetMapping("/userInformation")
    public ResponseEntity<byte[]> userInformation(HttpServletRequest request, HttpServletResponse response)   {
        try {
            return userServiceIMPL.exportUserInformation(request,response);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 导出用户信息
     */
    @ApiOperation("导出用户信息")
    @GetMapping("/outUserInformation")
    public ResponseEntity<byte[]> outUserInformation(HttpServletRequest request, HttpServletResponse response)   {
        try {
            return userServiceIMPL.outUserInformation(request,response);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}