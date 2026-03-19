package com.volunteer.volunteerplatform.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.volunteer.volunteerplatform.common.Result;
import com.volunteer.volunteerplatform.entity.SysUser;
import com.volunteer.volunteerplatform.service.IActivitySignupService;
import com.volunteer.volunteerplatform.service.IGoodsExchangeService;
import com.volunteer.volunteerplatform.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IActivitySignupService activitySignupService;

    @Autowired
    private IGoodsExchangeService goodsExchangeService;

    // 盐值，用于给 MD5 加严
    private static final String SALT = "VOLUNTEER_2026_SALT";

    @PostMapping("/login")
    public Result login(@RequestBody SysUser sysUser) {
        if (sysUser.getUsername() == null || sysUser.getPassword() == null) {
            return Result.error("用户名或密码不能为空");
        }

        String md5Password = DigestUtils.md5DigestAsHex((sysUser.getPassword() + SALT).getBytes());

        SysUser loginUser = userService.login(sysUser.getUsername(), md5Password);
        if (loginUser == null) return Result.error("用户名或密码错误");

        if (sysUser.getRole() != null) {
            String dbRole = loginUser.getRole();
            boolean roleMatch = sysUser.getRole().equals(dbRole) || ("ROLE_" + sysUser.getRole()).equals(dbRole);
            if (!roleMatch) return Result.error("身份选择错误，您没有该权限访问！");
        }
        return Result.success(loginUser);
    }

    @PostMapping("/register")
    public Result register(@RequestBody SysUser sysUser) {
        if (sysUser.getUsername() == null || sysUser.getPassword() == null || sysUser.getStudentId() == null) {
            return Result.error("必填项不能为空");
        }

        QueryWrapper<SysUser> nameQuery = new QueryWrapper<>();
        nameQuery.eq("username", sysUser.getUsername());
        if (userService.count(nameQuery) > 0) return Result.error("该登录账号已存在，请换一个");

        QueryWrapper<SysUser> studentIdQuery = new QueryWrapper<>();
        studentIdQuery.eq("student_id", sysUser.getStudentId());
        if (userService.count(studentIdQuery) > 0) return Result.error("该学号已注册过账号，请直接登录或找回密码");

        if (sysUser.getRole() == null) sysUser.setRole("USER");

        String md5Password = DigestUtils.md5DigestAsHex((sysUser.getPassword() + SALT).getBytes());
        sysUser.setPassword(md5Password);

        userService.save(sysUser);
        return Result.success();
    }

    @PostMapping
    public Result save(@RequestBody SysUser user) {
        if (user.getUsername() == null) return Result.error("参数错误");

        // 校验“登录账号”是否重复
        QueryWrapper<SysUser> nameQuery = new QueryWrapper<>();
        nameQuery.eq("username", user.getUsername());
        if (user.getId() != null) {
            nameQuery.ne("id", user.getId());
        }
        if (userService.count(nameQuery) > 0) {
            return Result.error("保存失败：该登录账号已被其他用户使用");
        }

        // 校验“学号/工号”是否重复
        if (user.getStudentId() != null && !user.getStudentId().isEmpty()) {
            QueryWrapper<SysUser> studentIdQuery = new QueryWrapper<>();
            studentIdQuery.eq("student_id", user.getStudentId());
            if (user.getId() != null) {
                studentIdQuery.ne("id", user.getId());
            }
            if (userService.count(studentIdQuery) > 0) {
                // 【核心修复】：根据角色动态返回提示文案
                String idName = "学号";
                if ("ADMIN".equals(user.getRole()) || "管理员".equals(user.getRole())) {
                    idName = "工号";
                }
                return Result.error("保存失败：该" + idName + "已存在，不可重复录入");
            }
        }

        // 密码加密处理
        if (user.getId() == null) {
            // 新增用户
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                user.setPassword("123456");
            }
            user.setPassword(DigestUtils.md5DigestAsHex((user.getPassword() + SALT).getBytes()));
        } else {
            // 修改用户：只有当输入了新密码（且没被加密过）时才加密
            if (user.getPassword() != null && user.getPassword().length() < 32) {
                user.setPassword(DigestUtils.md5DigestAsHex((user.getPassword() + SALT).getBytes()));
            }
        }

        userService.saveOrUpdate(user);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        SysUser user = userService.getById(id);
        if (user != null) {
            user.setPassword(null);
            return Result.success(user);
        }
        return Result.error("未找到该用户");
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String username) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        if (!"".equals(username)) queryWrapper.like("username", username);
        queryWrapper.orderByAsc("role").orderByDesc("id");
        return Result.success(userService.page(page, queryWrapper));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public Result delete(@PathVariable Integer id) {
        activitySignupService.remove(new QueryWrapper<com.volunteer.volunteerplatform.entity.ActivitySignup>().eq("user_id", id));
        goodsExchangeService.remove(new QueryWrapper<com.volunteer.volunteerplatform.entity.GoodsExchange>().eq("user_id", id));
        userService.removeById(id);
        return Result.success();
    }
}