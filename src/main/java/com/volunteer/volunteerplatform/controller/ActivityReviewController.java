package com.volunteer.volunteerplatform.controller;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.volunteer.volunteerplatform.common.Result;
import com.volunteer.volunteerplatform.entity.Activity;
import com.volunteer.volunteerplatform.entity.ActivityReview;
import com.volunteer.volunteerplatform.entity.SysUser; // 如果你的类叫 User 请自行修改
import com.volunteer.volunteerplatform.service.IActivityService;
import com.volunteer.volunteerplatform.service.IActivityReviewService;
import com.volunteer.volunteerplatform.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/review")
public class ActivityReviewController {

    @Autowired
    private IActivityReviewService reviewService;
    @Autowired
    private IUserService userService;
    @Autowired
    private IActivityService activityService;

    // 新增心得
    @PostMapping
    public Result save(@RequestBody ActivityReview review) {
        if (review.getId() == null) {
            review.setCreateTime(DateUtil.now());
        }
        reviewService.saveOrUpdate(review);
        return Result.success();
    }

    // 删除
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        reviewService.removeById(id);
        return Result.success();
    }

    // 分页查询（包含名字补全）
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        QueryWrapper<ActivityReview> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        Page<ActivityReview> page = reviewService.page(new Page<>(pageNum, pageSize), queryWrapper);

        for (ActivityReview record : page.getRecords()) {
            if (record.getUserId() != null) {
                SysUser user = userService.getById(record.getUserId());
                if (user != null) record.setNickname(user.getNickname());
            }
            if (record.getActivityId() != null) {
                Activity activity = activityService.getById(record.getActivityId());
                if (activity != null) record.setActivityName(activity.getName());
            }
        }
        return Result.success(page);
    }
}