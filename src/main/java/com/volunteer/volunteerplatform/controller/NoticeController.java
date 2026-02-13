package com.volunteer.volunteerplatform.controller;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.volunteer.volunteerplatform.common.Result;
import com.volunteer.volunteerplatform.entity.Notice;
import com.volunteer.volunteerplatform.service.INoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notice")
public class NoticeController {

    @Autowired
    private INoticeService noticeService;

    // 新增或修改
    @PostMapping
    public Result save(@RequestBody Notice notice) {
        // 如果是新增（ID为空）
        if (notice.getId() == null) {
            // 【核心逻辑】自动设置当前时间
            // DateUtil.now() 会自动获取服务器当前的北京时间 (格式: yyyy-MM-dd HH:mm:ss)
            notice.setTime(DateUtil.now());
        }
        noticeService.saveOrUpdate(notice);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        noticeService.removeById(id);
        return Result.success();
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String title) {
        Page<Notice> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Notice> queryWrapper = new QueryWrapper<>();
        if (!"".equals(title)) {
            queryWrapper.like("title", title);
        }
        queryWrapper.orderByDesc("id");
        return Result.success(noticeService.page(page, queryWrapper));
    }
}