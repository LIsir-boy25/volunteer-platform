package com.volunteer.volunteerplatform.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.volunteer.volunteerplatform.common.Result;
import com.volunteer.volunteerplatform.entity.Goods;
import com.volunteer.volunteerplatform.entity.SysUser;
import com.volunteer.volunteerplatform.service.IGoodsService;
import com.volunteer.volunteerplatform.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private IUserService userService;

    @PostMapping
    public Result save(@RequestBody Goods goods) {
        if (goods.getStatus() == null) {
            goods.setStatus("上架");
        }
        goodsService.saveOrUpdate(goods);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        goodsService.removeById(id);
        return Result.success();
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String name,
                           @RequestParam(defaultValue = "") String status) {
        QueryWrapper<Goods> queryWrapper = new QueryWrapper<>();
        if (!"".equals(name)) {
            queryWrapper.like("name", name);
        }
        if (!"".equals(status)) {
            queryWrapper.eq("status", status);
        }

        // 【关键修复】：改为正序排列，使 ID 顺序从小到大
        queryWrapper.orderByAsc("id");

        return Result.success(goodsService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    @PostMapping("/exchange/{userId}/{goodsId}")
    @Transactional
    public Result exchange(@PathVariable Integer userId, @PathVariable Integer goodsId) {
        Goods goods = goodsService.getById(goodsId);
        if (goods == null) return Result.error("该商品不存在！");
        if ("下架".equals(goods.getStatus())) return Result.error("该商品已下架！");
        if (goods.getStore() <= 0) return Result.error("库存不足！");

        SysUser user = userService.getById(userId);
        if (user == null) return Result.error("用户异常！");
        if (user.getScore() < goods.getScore()) return Result.error("积分不足！");

        user.setScore(user.getScore() - goods.getScore());
        userService.updateById(user);

        goods.setStore(goods.getStore() - 1);
        goodsService.updateById(goods);

        return Result.success();
    }
}