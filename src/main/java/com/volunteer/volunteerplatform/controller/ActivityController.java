package com.volunteer.volunteerplatform.controller;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.volunteer.volunteerplatform.common.Result;
import com.volunteer.volunteerplatform.entity.Activity;
import com.volunteer.volunteerplatform.service.IActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List; // ✅ 补充导入 List，防止报错

@RestController
@RequestMapping("/activity")
public class ActivityController {

    @Autowired
    private IActivityService activityService;

    // 新增或者更新
    @PostMapping
    public Result save(@RequestBody Activity activity) {
        if (activity.getId() == null) {
            activity.setStatus("报名中"); // 默认状态
        }
        activityService.saveOrUpdate(activity);
        return Result.success();
    }

    // 单条删除
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        activityService.removeById(id);
        return Result.success();
    }

    // 批量删除接口
    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        activityService.removeByIds(ids);
        return Result.success();
    }

    // 分页查询
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

    // ✅ 新增：根据 ID 查询单条活动详情
    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        return Result.success(activityService.getById(id));
    }
}