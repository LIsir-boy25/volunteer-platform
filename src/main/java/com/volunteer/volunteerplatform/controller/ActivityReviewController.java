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

import java.util.List;

@RestController
@RequestMapping("/review")
public class ActivityReviewController {

    @Autowired
    private IActivityReviewService reviewService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IActivityService activityService;

    /**
     * 1. 保存心得（带重复校验）
     */
    @PostMapping
    public Result save(@RequestBody ActivityReview review) {
        // 🌟 核心逻辑：如果是新增（id为空），先检查数据库是否已经存在该用户对该活动的心得
        if (review.getId() == null) {
            QueryWrapper<ActivityReview> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", review.getUserId());
            queryWrapper.eq("activity_id", review.getActivityId());

            if (reviewService.getOne(queryWrapper) != null) {
                // 🌟 优雅报错：不再弹出红色天书，而是返回一个温馨提示
                return Result.error("400", "您已经发布过该活动的心得，请勿重复发布！");
            }
            review.setCreateTime(DateUtil.now());
        }

        // 执行保存或更新
        reviewService.saveOrUpdate(review);
        return Result.success();
    }

    /**
     * 🌟 新增接口：根据用户ID和活动ID获取心得内容
     * 用于前端判断“是否已发过”以及“查看我的心得”功能
     */
    @GetMapping("/getByUser")
    public Result getByUser(@RequestParam Integer activityId, @RequestParam Integer userId) {
        QueryWrapper<ActivityReview> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("activity_id", activityId);
        ActivityReview review = reviewService.getOne(queryWrapper);
        return Result.success(review);
    }

    // 2. 删除心得
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        reviewService.removeById(id);
        return Result.success();
    }

    // 3. 分页查询（保持您原有的逻辑）
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String activityName) {

        QueryWrapper<ActivityReview> queryWrapper = new QueryWrapper<>();

        if (!"".equals(activityName)) {
            queryWrapper.inSql("activity_id", "select id from activity where name like '%" + activityName + "%'");
        }

        queryWrapper.orderByDesc("id");
        Page<ActivityReview> page = reviewService.page(new Page<>(pageNum, pageSize), queryWrapper);

        for (ActivityReview record : page.getRecords()) {
            if (record.getUserId() != null) {
                SysUser user = userService.getById(record.getUserId());
                if (user != null) {
                    record.setNickname(user.getNickname());
                    record.setAvatar(user.getAvatar());
                }
            }
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