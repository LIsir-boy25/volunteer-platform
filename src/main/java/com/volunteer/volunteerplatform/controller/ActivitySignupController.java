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

    @PostMapping
    public Result save(@RequestBody ActivitySignup activitySignup) {
        if (activitySignup.getId() == null) {
            // 1. 重复报名校验
            QueryWrapper<ActivitySignup> query = new QueryWrapper<>();
            query.eq("user_id", activitySignup.getUserId());
            query.eq("activity_id", activitySignup.getActivityId());
            if (activitySignupService.count(query) > 0) {
                return Result.error("您已报名过该活动，请勿重复报名！");
            }

            // 2. 【容量闭环核心】：检查活动名额是否已满
            Activity activity = activityService.getById(activitySignup.getActivityId());
            if (activity == null) return Result.error("活动不存在！");

            // 统计当前已占坑的人数（待审核、审核通过、已完成的都算占用名额）
            QueryWrapper<ActivitySignup> countQuery = new QueryWrapper<>();
            countQuery.eq("activity_id", activitySignup.getActivityId());
            countQuery.ne("status", "审核不通过");
            long signedCount = activitySignupService.count(countQuery);

            // 【修复点】：直接使用 activity.getNum() 进行比较，不再调用 parseInt
            if (activity.getNum() != null && signedCount >= activity.getNum()) {
                return Result.error("非常抱歉，该活动名额已满！");
            }

            // 3. 补全用户信息并保存
            SysUser user = userService.getById(activitySignup.getUserId());
            if (user != null) {
                activitySignup.setNickname(user.getNickname());
                activitySignup.setStudentId(user.getStudentId());
            }
            activitySignup.setActivityName(activity.getName());
            activitySignup.setSignupTime(DateUtil.now());
            activitySignup.setStatus("待审核");
            activitySignupService.save(activitySignup);

        } else {
            // 状态修改与积分发放逻辑
            if ("已完成".equals(activitySignup.getStatus())) {
                ActivitySignup dbRecord = activitySignupService.getById(activitySignup.getId());
                if (dbRecord != null && !"已完成".equals(dbRecord.getStatus())) {
                    Activity activity = activityService.getById(dbRecord.getActivityId());
                    SysUser user = userService.getById(dbRecord.getUserId());
                    if (activity != null && user != null && activity.getCredit() != null) {
                        int currentScore = user.getScore() == null ? 0 : user.getScore();
                        user.setScore(currentScore + activity.getCredit());
                        userService.updateById(user);
                    }
                }
            }
            activitySignupService.updateById(activitySignup);
        }
        return Result.success();
    }

    @GetMapping
    public Result findAll(@RequestParam(required = false) Integer userId) {
        QueryWrapper<ActivitySignup> queryWrapper = new QueryWrapper<>();
        if (userId != null) {
            queryWrapper.eq("user_id", userId);
        }
        queryWrapper.orderByDesc("id");
        return Result.success(activitySignupService.list(queryWrapper));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum, @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String name, @RequestParam(required = false) Integer userId) {
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

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        activitySignupService.removeById(id);
        return Result.success();
    }
}