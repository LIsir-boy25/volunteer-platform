package com.volunteer.volunteerplatform.controller;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.volunteer.volunteerplatform.common.Result;
import com.volunteer.volunteerplatform.entity.Feedback;
import com.volunteer.volunteerplatform.entity.SysUser; // 引入用户实体
import com.volunteer.volunteerplatform.service.IFeedbackService;
import com.volunteer.volunteerplatform.service.IUserService; // 引入用户服务
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    @Autowired
    private IFeedbackService feedbackService;

    @Autowired
    private IUserService userService; // 【新增】：注入用户服务，用来查名字

    // 1. 提交反馈 / 更新反馈
    @PostMapping
    public Result save(@RequestBody Feedback feedback) {
        // 如果是新增反馈，自动补充当前时间
        if (feedback.getId() == null) {
            feedback.setCreateTime(DateUtil.now());
        }
        feedbackService.saveOrUpdate(feedback);
        return Result.success();
    }

    // 2. 专门的回复接口 (供管理员后台使用)
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

    // 4. 分页查询 (包含名字补全和模糊搜索)
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String content) { // 【新增】：接收前端的搜索词

        QueryWrapper<Feedback> queryWrapper = new QueryWrapper<>();

        // 【新增】：处理搜索逻辑
        if (!"".equals(content)) {
            queryWrapper.like("content", content);
        }

        // 保证最新的反馈展示在最前面
        queryWrapper.orderByDesc("id");
        Page<Feedback> page = feedbackService.page(new Page<>(pageNum, pageSize), queryWrapper);

        // 【核心修复】：循环补全反馈人的名字
        for (Feedback record : page.getRecords()) {
            if (record.getUserId() != null) {
                SysUser user = userService.getById(record.getUserId());
                if (user != null) {
                    record.setNickname(user.getNickname());
                }
            }
        }
        return Result.success(page);
    }
}