package com.volunteer.volunteerplatform.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.volunteer.volunteerplatform.common.Result;
import com.volunteer.volunteerplatform.entity.SysUser;
import com.volunteer.volunteerplatform.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService userService;

    // 1. 登录
    @PostMapping("/login")
    public Result login(@RequestBody SysUser sysUser) {
        if (sysUser.getUsername() == null || sysUser.getPassword() == null) {
            return Result.error("用户名或密码不能为空");
        }
        SysUser loginUser = userService.login(sysUser.getUsername(), sysUser.getPassword());
        return Result.success(loginUser);
    }

    // 2. 注册 (增加了账号名唯一和学号唯一双重校验)
    @PostMapping("/register")
    public Result register(@RequestBody SysUser sysUser) {
        // a. 基本非空校验
        if (sysUser.getUsername() == null || sysUser.getPassword() == null || sysUser.getStudentId() == null) {
            return Result.error("必填项不能为空");
        }

        // b. 校验登录账号(username)是否已存在
        QueryWrapper<SysUser> nameQuery = new QueryWrapper<>();
        nameQuery.eq("username", sysUser.getUsername());
        if (userService.count(nameQuery) > 0) {
            return Result.error("该登录账号已存在，请换一个");
        }

        // c. 【核心新增】校验学号(studentId)是否已存在
        QueryWrapper<SysUser> studentIdQuery = new QueryWrapper<>();
        studentIdQuery.eq("student_id", sysUser.getStudentId());
        if (userService.count(studentIdQuery) > 0) {
            return Result.error("该学号已注册过账号，请直接登录或找回密码");
        }

        // d. 执行保存
        userService.save(sysUser);
        return Result.success();
    }

    // 3. 修改资料 (个人中心使用)
    @PostMapping("/update")
    public Result update(@RequestBody SysUser user) {
        if (user.getId() == null) {
            return Result.error("参数错误");
        }
        userService.updateById(user);
        return Result.success();
    }

    // 4. 根据ID查询 (解决个人中心404)
    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        SysUser user = userService.getById(id);
        if (user != null) {
            user.setPassword(null); // 不返回密码
            return Result.success(user);
        }
        return Result.error("未找到该用户");
    }

    // 5. 分页查询 (管理端)
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String username) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        if (!"".equals(username)) {
            queryWrapper.like("username", username);
        }
        queryWrapper.orderByDesc("id");
        return Result.success(userService.page(page, queryWrapper));
    }

    // 6. 删除
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        userService.removeById(id);
        return Result.success();
    }
}