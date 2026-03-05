package com.volunteer.volunteerplatform.controller;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.volunteer.volunteerplatform.common.Result;
import com.volunteer.volunteerplatform.entity.Activity;
import com.volunteer.volunteerplatform.entity.ActivityReview;
import com.volunteer.volunteerplatform.entity.SysUser;
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

    // 1. 新增心得
    @PostMapping
    public Result save(@RequestBody ActivityReview review) {
        if (review.getId() == null) {
            review.setCreateTime(DateUtil.now());
        }
        reviewService.saveOrUpdate(review);
        return Result.success();
    }

    // 2. 删除心得
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        reviewService.removeById(id);
        return Result.success();
    }

    // 3. 分页查询（包含跨表搜索与名字补全）
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String activityName) { // 【新增】：接收前端传来的搜索词

        QueryWrapper<ActivityReview> queryWrapper = new QueryWrapper<>();

        // 【核心优化】：解决跨表搜索！如果前端输入了活动主题，利用子查询去活动表里反查活动 ID
        if (!"".equals(activityName)) {
            queryWrapper.inSql("activity_id", "select id from activity where name like '%" + activityName + "%'");
        }

        queryWrapper.orderByDesc("id");
        Page<ActivityReview> page = reviewService.page(new Page<>(pageNum, pageSize), queryWrapper);

        // 循环补全信息给前端显示
        for (ActivityReview record : page.getRecords()) {
            // 补全发布人姓名
            if (record.getUserId() != null) {
                SysUser user = userService.getById(record.getUserId());
                if (user != null) {
                    record.setNickname(user.getNickname());
                }
            }
            // 补全活动主题名称
            if (record.getActivityId() != null) {
                Activity activity = activityService.getById(record.getActivityId());
                if (activity != null) {
                    record.setActivityName(activity.getName());
                }
            }
        }
        return Result.success(page);
    }
}