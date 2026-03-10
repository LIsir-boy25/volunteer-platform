package com.volunteer.volunteerplatform.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.volunteer.volunteerplatform.common.Result;
import com.volunteer.volunteerplatform.entity.GoodsExchange;
import com.volunteer.volunteerplatform.entity.SysUser;
import com.volunteer.volunteerplatform.service.IGoodsExchangeService;
import com.volunteer.volunteerplatform.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/goodsExchange")
public class GoodsExchangeController {

    @Autowired
    private IGoodsExchangeService goodsExchangeService;

    // 【新增】引入用户服务
    @Autowired
    private IUserService userService;

    @PostMapping
    public Result save(@RequestBody GoodsExchange goodsExchange) {
        goodsExchangeService.saveOrUpdate(goodsExchange);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        goodsExchangeService.removeById(id);
        return Result.success();
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String goodsName,
                           @RequestParam(required = false) Integer userId) {
        QueryWrapper<GoodsExchange> queryWrapper = new QueryWrapper<>();
        if (!"".equals(goodsName)) {
            queryWrapper.like("goods_name", goodsName);
        }
        if (userId != null) {
            queryWrapper.eq("user_id", userId);
        }
        queryWrapper.orderByDesc("id");

        // 1. 先查出原始的分页数据
        Page<GoodsExchange> page = goodsExchangeService.page(new Page<>(pageNum, pageSize), queryWrapper);

        // 2. 【核心修改】：遍历这些数据，根据 userId 动态去把名字查出来
        List<GoodsExchange> records = page.getRecords();
        for (GoodsExchange record : records) {
            SysUser user = userService.getById(record.getUserId());
            if (user != null) {
                // 优先显示真实姓名，如果没有再显示登录账号
                record.setUserName(user.getNickname() != null ? user.getNickname() : user.getUsername());
            } else {
                record.setUserName("已注销用户");
            }
        }

        return Result.success(page);
    }
}