package com.volunteer.volunteerplatform.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.volunteer.volunteerplatform.common.Result;
import com.volunteer.volunteerplatform.entity.Activity;
import com.volunteer.volunteerplatform.service.IActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/activity")
public class ActivityController {

    @Autowired
    private IActivityService activityService;

    // 1. 新增或更新
    @PostMapping
    public Result save(@RequestBody Activity activity) {
        if (activity.getId() == null) {
            activity.setStatus("报名中");
        }
        activityService.saveOrUpdate(activity);
        return Result.success();
    }

    // 2. 删除
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        activityService.removeById(id);
        return Result.success();
    }

    // 3. 【关键修复】分页查询
    // 必须加上 "/page"，这样系统就不会把它误认为是 ID 了
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String name) {
        Page<Activity> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Activity> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        if (!"".equals(name)) {
            queryWrapper.like("name", name);
        }
        return Result.success(activityService.page(page, queryWrapper));
    }

    // 4. 根据ID查询 (如果你之前有这个方法，它就是导致冲突的“罪魁祸首”)
    // 把它放在 /page 后面，或者暂时删掉也可以
    @GetMapping("/{id}")
    public Result findById(@PathVariable Integer id) {
        return Result.success(activityService.getById(id));
    }
}