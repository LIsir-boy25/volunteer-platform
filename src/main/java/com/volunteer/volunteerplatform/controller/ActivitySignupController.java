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

@RestController
@RequestMapping("/activitySignup")
public class ActivitySignupController {

    @Autowired
    private IActivitySignupService activitySignupService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IActivityService activityService;

    // 1. 用户报名与审核/签到（包含自动发积分逻辑）
    @PostMapping
    public Result save(@RequestBody ActivitySignup activitySignup) {

        if (activitySignup.getId() == null) {
            // ================= 【新增报名逻辑】 =================
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

            activitySignupService.save(activitySignup);

        } else {
            // ================= 【状态修改/签到/审核逻辑】 =================

            // 【核心修复】：如果是签到/完成操作，自动给志愿者发放积分！
            if ("已完成".equals(activitySignup.getStatus())) {
                // 1. 先去数据库查一下这条记录之前的状态，防止黑客恶意重复调用接口无限刷分
                ActivitySignup dbRecord = activitySignupService.getById(activitySignup.getId());

                if (dbRecord != null && !"已完成".equals(dbRecord.getStatus())) {
                    // 2. 只有从“非完成”变成“已完成”，才发积分！查出这个活动值多少分
                    Activity activity = activityService.getById(dbRecord.getActivityId());
                    // 3. 查出这个志愿者目前的积分
                    SysUser user = userService.getById(dbRecord.getUserId());

                    if (activity != null && user != null && activity.getCredit() != null) {
                        // 4. 计算新积分并更新到用户表
                        int currentScore = user.getScore() == null ? 0 : user.getScore();
                        user.setScore(currentScore + activity.getCredit());
                        userService.updateById(user);
                    }
                }
            }

            // 最后更新这条报名记录的状态为“已完成”或其他状态
            activitySignupService.updateById(activitySignup);
        }

        return Result.success();
    }

    // 2. 查询报名列表
    @GetMapping
    public Result findAll(@RequestParam(required = false) Integer userId) {
        QueryWrapper<ActivitySignup> queryWrapper = new QueryWrapper<>();
        if (userId != null) {
            queryWrapper.eq("user_id", userId);
        }
        queryWrapper.orderByDesc("id");
        return Result.success(activitySignupService.list(queryWrapper));
    }

    // 3. 分页查询
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String name,
                           @RequestParam(required = false) Integer userId) {
        QueryWrapper<ActivitySignup> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");

        if (!"".equals(name)) {
            queryWrapper.and(wrapper -> wrapper.like("nickname", name).or().like("activity_name", name));
        }

        if (userId != null) {
            queryWrapper.eq("user_id", userId);
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