package com.volunteer.volunteerplatform.controller;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.volunteer.volunteerplatform.common.Result;
import com.volunteer.volunteerplatform.entity.Activity;
import com.volunteer.volunteerplatform.entity.ActivityCollect;
import com.volunteer.volunteerplatform.service.IActivityService;
import com.volunteer.volunteerplatform.service.IActivityCollectService; // 【补上的关键代码：引入收藏Service】
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/collect")
public class ActivityCollectController {

    @Autowired
    private IActivityCollectService collectService;

    @Autowired
    private IActivityService activityService;

    // 1. 收藏 或 取消收藏 (前端点击同一个按钮触发)
    @PostMapping
    public Result save(@RequestBody ActivityCollect collect) {
        // 先查询数据库里是不是已经收藏过了
        QueryWrapper<ActivityCollect> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", collect.getUserId());
        queryWrapper.eq("activity_id", collect.getActivityId());
        ActivityCollect dbCollect = collectService.getOne(queryWrapper);

        if (dbCollect != null) {
            // 如果已经收藏了，再次点击就是【取消收藏】
            collectService.removeById(dbCollect.getId());
            return Result.success("已取消收藏");
        } else {
            // 如果没收藏，就是【新增收藏】
            collect.setTime(DateUtil.now());
            collectService.save(collect);
            return Result.success("收藏成功");
        }
    }

    // 2. 在收藏列表里彻底删除
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        collectService.removeById(id);
        return Result.success();
    }

    // 3. 分页查询“我的收藏” (同时查出活动的主题和图片)
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam Integer userId) {
        QueryWrapper<ActivityCollect> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.orderByDesc("id");
        Page<ActivityCollect> page = collectService.page(new Page<>(pageNum, pageSize), queryWrapper);

        // 循环补全活动的名称和封面图，方便前端展示
        for (ActivityCollect record : page.getRecords()) {
            if (record.getActivityId() != null) {
                Activity activity = activityService.getById(record.getActivityId());
                if (activity != null) {
                    record.setActivityName(activity.getName());
                    record.setActivityImg(activity.getImg());
                }
            }
        }
        return Result.success(page);
    }
}