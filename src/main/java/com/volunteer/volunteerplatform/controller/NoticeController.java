package com.volunteer.volunteerplatform.controller;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.volunteer.volunteerplatform.common.Result;
import com.volunteer.volunteerplatform.entity.Notice;
import com.volunteer.volunteerplatform.service.INoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notice")
public class NoticeController {

    @Autowired
    private INoticeService noticeService;

    // 1. 新增或修改公告
    @PostMapping
    public Result save(@RequestBody Notice notice) {
        if (notice.getId() == null) {
            notice.setTime(DateUtil.now());
        }
        noticeService.saveOrUpdate(notice);
        return Result.success();
    }

    // 2. 删除公告
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        noticeService.removeById(id);
        return Result.success();
    }

    // 3. 【核心修复】查询所有公告接口 (解决 405 报错)
    @GetMapping
    public Result findAll(@RequestParam(defaultValue = "") String title) {
        QueryWrapper<Notice> queryWrapper = new QueryWrapper<>();
        if (!"".equals(title)) {
            queryWrapper.like("title", title);
        }
        queryWrapper.orderByDesc("id"); // 最新公告在最前
        return Result.success(noticeService.list(queryWrapper));
    }
}