package com.volunteer.volunteerplatform.controller;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.volunteer.volunteerplatform.common.Result;
import com.volunteer.volunteerplatform.entity.Activity;
import com.volunteer.volunteerplatform.entity.ActivitySignup;
import com.volunteer.volunteerplatform.entity.SysUser;
import com.volunteer.volunteerplatform.service.IActivityService;
import com.volunteer.volunteerplatform.service.IActivitySignupService;
import com.volunteer.volunteerplatform.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/activitySignup")
public class ActivitySignupController {

    @Autowired
    private IActivitySignupService activitySignupService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IActivityService activityService;

    // 1. 用户报名（志愿者前台使用）
    @PostMapping
    public Result save(@RequestBody ActivitySignup activitySignup) {
        if (activitySignup.getId() == null) {
            QueryWrapper<ActivitySignup> query = new QueryWrapper<>();
            query.eq("user_id", activitySignup.getUserId());
            query.eq("activity_id", activitySignup.getActivityId());
            if (activitySignupService.count(query) > 0) {
                return Result.error("您已报名过该活动，请勿重复报名！");
            }

            SysUser user = userService.getById(activitySignup.getUserId());
            if (user != null) {
                activitySignup.setNickname(user.getNickname());
                activitySignup.setStudentId(user.getStudentId());
            }

            Activity activity = activityService.getById(activitySignup.getActivityId());
            if (activity != null) {
                activitySignup.setActivityName(activity.getName());
            }

            activitySignup.setSignupTime(DateUtil.now());
            activitySignup.setStatus("待审核");
        }
        activitySignupService.saveOrUpdate(activitySignup);
        return Result.success();
    }

    // 2. 【核心修复】查询报名列表（支持按 userId 过滤，供“我的报名”使用）
    @GetMapping
    public Result findAll(@RequestParam(required = false) Integer userId) {
        QueryWrapper<ActivitySignup> queryWrapper = new QueryWrapper<>();
        if (userId != null) {
            queryWrapper.eq("user_id", userId);
        }
        queryWrapper.orderByDesc("id");
        return Result.success(activitySignupService.list(queryWrapper));
    }

    // 3. 分页查询（管理员后台使用）
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String name) {
        QueryWrapper<ActivitySignup> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        if (!"".equals(name)) {
            queryWrapper.and(wrapper -> wrapper.like("nickname", name).or().like("activity_name", name));
        }
        return Result.success(activitySignupService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    // 4. 删除/取消报名
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        activitySignupService.removeById(id);
        return Result.success();
    }
}