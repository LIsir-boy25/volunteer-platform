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

        if (loginUser == null) {
            return Result.error("用户名或密码错误");
        }

        // 【核心修复：向下兼容老数据的越权拦截】
        if (sysUser.getRole() != null) {
            String dbRole = loginUser.getRole();
            // 兼容判断：前端传 ADMIN，能匹配数据库的 ADMIN 也能匹配老数据 ROLE_ADMIN
            boolean roleMatch = sysUser.getRole().equals(dbRole) || ("ROLE_" + sysUser.getRole()).equals(dbRole);

            if (!roleMatch) {
                return Result.error("身份选择错误，您没有该权限访问！");
            }
        }

        return Result.success(loginUser);
    }

    // 2. 注册
    @PostMapping("/register")
    public Result register(@RequestBody SysUser sysUser) {
        if (sysUser.getUsername() == null || sysUser.getPassword() == null || sysUser.getStudentId() == null) {
            return Result.error("必填项不能为空");
        }

        QueryWrapper<SysUser> nameQuery = new QueryWrapper<>();
        nameQuery.eq("username", sysUser.getUsername());
        if (userService.count(nameQuery) > 0) {
            return Result.error("该登录账号已存在，请换一个");
        }

        QueryWrapper<SysUser> studentIdQuery = new QueryWrapper<>();
        studentIdQuery.eq("student_id", sysUser.getStudentId());
        if (userService.count(studentIdQuery) > 0) {
            return Result.error("该学号已注册过账号，请直接登录或找回密码");
        }

        if (sysUser.getRole() == null) {
            sysUser.setRole("USER"); // 现在新注册的都统一存简洁版 USER
        }

        userService.save(sysUser);
        return Result.success();
    }

    // 3. 新增或修改资料
    @PostMapping
    public Result save(@RequestBody SysUser user) {
        if (user.getUsername() == null) {
            return Result.error("参数错误");
        }
        userService.saveOrUpdate(user);
        return Result.success();
    }

    // 4. 根据ID查询
    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        SysUser user = userService.getById(id);
        if (user != null) {
            user.setPassword(null);
            return Result.success(user);
        }
        return Result.error("未找到该用户");
    }

    // 5. 分页查询
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