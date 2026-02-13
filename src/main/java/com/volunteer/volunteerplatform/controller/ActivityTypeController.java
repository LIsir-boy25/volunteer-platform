package com.volunteer.volunteerplatform.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.volunteer.volunteerplatform.common.Result;
import com.volunteer.volunteerplatform.entity.ActivityType;
import com.volunteer.volunteerplatform.service.IActivityTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/activity-type")
public class ActivityTypeController {

    @Autowired
    private IActivityTypeService activityTypeService;

    // 1. 新增或修改
    @PostMapping
    public Result save(@RequestBody ActivityType activityType) {
        activityTypeService.saveOrUpdate(activityType);
        return Result.success();
    }

    // 2. 删除
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        activityTypeService.removeById(id);
        return Result.success();
    }

    // 3. 查询所有（这个接口留着，以后发布活动的时候，下拉框选类型要用）
    @GetMapping
    public Result findAll() {
        return Result.success(activityTypeService.list());
    }

    // 4. 【核心新增】分页查询接口
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String name) {
        Page<ActivityType> page = new Page<>(pageNum, pageSize);
        QueryWrapper<ActivityType> queryWrapper = new QueryWrapper<>();
        // 如果搜索框有内容，就模糊查询名称
        if (!"".equals(name)) {
            queryWrapper.like("name", name);
        }
        queryWrapper.orderByDesc("id"); // 倒序排列
        return Result.success(activityTypeService.page(page, queryWrapper));
    }
}