package com.volunteer.volunteerplatform.controller;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.volunteer.volunteerplatform.common.Result;
import com.volunteer.volunteerplatform.entity.Activity;
import com.volunteer.volunteerplatform.entity.ActivityReview;
import com.volunteer.volunteerplatform.entity.ActivitySignup;
import com.volunteer.volunteerplatform.entity.SysUser;
import com.volunteer.volunteerplatform.service.IActivityService;
import com.volunteer.volunteerplatform.service.IActivitySignupService;
import com.volunteer.volunteerplatform.service.IActivityReviewService;
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

    @Autowired
    private IActivityReviewService reviewService; // 🌟 注入心得服务，用于状态核对

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

            // 2. 检查活动名额是否已满
            Activity activity = activityService.getById(activitySignup.getActivityId());
            if (activity == null) return Result.error("活动不存在！");

            QueryWrapper<ActivitySignup> countQuery = new QueryWrapper<>();
            countQuery.eq("activity_id", activitySignup.getActivityId());
            countQuery.ne("status", "审核不通过");
            long signedCount = activitySignupService.count(countQuery);

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

    /**
     * 🌟 修改后的查询：为移动端或简单列表补全 hasReview 🌟
     */
    @GetMapping
    public Result findAll(@RequestParam(required = false) Integer userId) {
        QueryWrapper<ActivitySignup> queryWrapper = new QueryWrapper<>();
        if (userId != null) {
            queryWrapper.eq("user_id", userId);
        }
        queryWrapper.orderByDesc("id");
        List<ActivitySignup> list = activitySignupService.list(queryWrapper);

        // 遍历设置心得状态
        for (ActivitySignup record : list) {
            record.setHasReview(checkReviewStatus(record.getUserId(), record.getActivityId()));
        }
        return Result.success(list);
    }

    /**
     * 🌟 修改后的分页查询：核心修复点 🌟
     * 遍历分页结果中的每一条记录，核对是否已发心得
     */
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

        // 1. 获取原始分页数据
        Page<ActivitySignup> page = activitySignupService.page(new Page<>(pageNum, pageSize), queryWrapper);

        // 2. 🌟 遍历 records，根据 userId 和 activityId 补全 hasReview 状态
        for (ActivitySignup record : page.getRecords()) {
            // 只有当状态为“已完成”时，检查心得状态才有意义
            if ("已完成".equals(record.getStatus())) {
                record.setHasReview(checkReviewStatus(record.getUserId(), record.getActivityId()));
            } else {
                record.setHasReview(false);
            }
        }

        return Result.success(page);
    }

    /**
     * 🌟 私有辅助方法：从数据库核对心得记录是否存在
     */
    private Boolean checkReviewStatus(Integer userId, Integer activityId) {
        if (userId == null || activityId == null) return false;
        QueryWrapper<ActivityReview> qw = new QueryWrapper<>();
        qw.eq("user_id", userId);
        qw.eq("activity_id", activityId);
        // 如果能查到记录（count > 0），则表示已经发布过心得
        return reviewService.count(qw) > 0;
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        activitySignupService.removeById(id);
        return Result.success();
    }
}