package com.volunteer.volunteerplatform.controller;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.volunteer.volunteerplatform.common.Result;
import com.volunteer.volunteerplatform.entity.Goods;
import com.volunteer.volunteerplatform.entity.GoodsExchange;
import com.volunteer.volunteerplatform.entity.SysUser;
import com.volunteer.volunteerplatform.service.IGoodsExchangeService;
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

    @Autowired
    private IGoodsExchangeService goodsExchangeService;

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
        queryWrapper.orderByDesc("id");
        return Result.success(goodsService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    // 【核心改造】：改成接收 GoodsExchange 实体，里面包含了前端传来的地址和电话
    @PostMapping("/exchange")
    @Transactional
    public Result exchange(@RequestBody GoodsExchange exchange) {
        Integer userId = exchange.getUserId();
        Integer goodsId = exchange.getGoodsId();

        Goods goods = goodsService.getById(goodsId);
        if (goods == null) return Result.error("该商品不存在！");
        if ("下架".equals(goods.getStatus())) return Result.error("该商品已下架！");
        if (goods.getStore() <= 0) return Result.error("手慢了，商品已兑完！");

        SysUser user = userService.getById(userId);
        if (user == null) return Result.error("用户异常！");
        if (user.getScore() < goods.getScore()) return Result.error("您的积分不足以兑换此商品！");

        // 1. 扣分减库存
        user.setScore(user.getScore() - goods.getScore());
        userService.updateById(user);

        goods.setStore(goods.getStore() - 1);
        goodsService.updateById(goods);

        // 2. 补全兑换订单信息（包含了前端传来的 phone 和 address）并保存
        exchange.setGoodsName(goods.getName());
        exchange.setGoodsImg(goods.getImg());
        exchange.setScore(goods.getScore());
        exchange.setCreateTime(DateUtil.now());
        exchange.setStatus("待处理");

        goodsExchangeService.save(exchange);

        return Result.success();
    }
}