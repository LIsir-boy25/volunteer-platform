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
@RequestMapping("/activityType")
public class ActivityTypeController {

    @Autowired
    private IActivityTypeService activityTypeService;

    // 分页查询
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String name) {
        Page<ActivityType> page = new Page<>(pageNum, pageSize);
        QueryWrapper<ActivityType> queryWrapper = new QueryWrapper<>();
        if (!"".equals(name)) {
            queryWrapper.like("name", name);
        }
        return Result.success(activityTypeService.page(page, queryWrapper));
    }

    // 查询所有（给后续"发布活动"的下拉框用）
    @GetMapping
    public Result findAll() {
        return Result.success(activityTypeService.list());
    }

    // 新增或更新
    @PostMapping
    public Result save(@RequestBody ActivityType activityType) {
        activityTypeService.saveOrUpdate(activityType);
        return Result.success();
    }

    // 删除
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        activityTypeService.removeById(id);
        return Result.success();
    }
}