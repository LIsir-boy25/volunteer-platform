package com.volunteer.volunteerplatform.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.volunteer.volunteerplatform.common.Result;
import com.volunteer.volunteerplatform.entity.Activity;
import com.volunteer.volunteerplatform.service.IActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/activity")
public class ActivityController {

    @Autowired
    private IActivityService activityService;

    // 1. 新增或更新活动
    @PostMapping
    public Result save(@RequestBody Activity activity) {
        activityService.saveOrUpdate(activity);
        return Result.success();
    }

    // 2. 删除活动
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        activityService.removeById(id);
        return Result.success();
    }

    // 3. 批量删除 (保留着以备不时之需)
    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        activityService.removeByIds(ids);
        return Result.success();
    }

    // 4. 【核心】查询所有数据 (对应前端 axios.get("/api/activity"))
    @GetMapping
    public Result findAll() {
        // 为了体验好一点，还是按 ID 倒序排一下，让新加的在最前面
        QueryWrapper<Activity> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        return Result.success(activityService.list(queryWrapper));
    }

    // 5. 根据ID查询单个 (详情页用)
    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(activityService.getById(id));
    }
}