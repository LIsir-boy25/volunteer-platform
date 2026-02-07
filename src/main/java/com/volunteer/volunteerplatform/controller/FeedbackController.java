package com.volunteer.volunteerplatform.controller;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.volunteer.volunteerplatform.common.Result;
import com.volunteer.volunteerplatform.entity.Feedback;
import com.volunteer.volunteerplatform.service.IFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    @Autowired
    private IFeedbackService feedbackService;

    // 1. 提交反馈 / 回复反馈
    @PostMapping
    public Result save(@RequestBody Feedback feedback) {
        if (feedback.getId() == null) {
            feedback.setCreateTime(DateUtil.now()); // 新增时设置时间
        }
        feedbackService.saveOrUpdate(feedback);
        return Result.success();
    }

    // 2. 专门的回复接口 (供管理员用)
    @PostMapping("/reply")
    public Result reply(@RequestBody Feedback feedback) {
        feedbackService.updateById(feedback);
        return Result.success();
    }

    // 3. 删除反馈
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        feedbackService.removeById(id);
        return Result.success();
    }

    // 4. 分页查询
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {
        QueryWrapper<Feedback> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id"); // 最新留言在最前
        return Result.success(feedbackService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }
}