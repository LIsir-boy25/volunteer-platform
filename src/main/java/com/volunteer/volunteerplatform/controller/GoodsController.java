package com.volunteer.volunteerplatform.controller;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
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

    // 【核心改造：防超卖与积分并发控制机制】
    @PostMapping("/exchange")
    @Transactional(rollbackFor = Exception.class) // 开启事务，遇到报错自动回滚
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

        // 1. 扣减库存 (利用数据库排他锁防超卖)
        UpdateWrapper<Goods> goodsUpdateWrapper = new UpdateWrapper<>();
        goodsUpdateWrapper.eq("id", goods.getId())
                .gt("store", 0) // 并发时确保库存>0
                .setSql("store = store - 1");
        boolean updateGoods = goodsService.update(goodsUpdateWrapper);
        if (!updateGoods) {
            throw new RuntimeException("手慢了，商品刚刚被抢光！"); // 抛出异常触发回滚
        }

        // 2. 扣减积分 (利用数据库并发锁防透支)
        UpdateWrapper<SysUser> userUpdateWrapper = new UpdateWrapper<>();
        userUpdateWrapper.eq("id", user.getId())
                .ge("score", goods.getScore()) // 并发时确保余额仍然充足
                .setSql("score = score - " + goods.getScore());
        boolean updateUser = userService.update(userUpdateWrapper);
        if (!updateUser) {
            throw new RuntimeException("您的积分在其他页面发生变动，余额不足！"); // 抛出异常触发回滚
        }

        // 3. 补全兑换订单信息并保存流水
        exchange.setGoodsName(goods.getName());
        exchange.setGoodsImg(goods.getImg());
        exchange.setScore(goods.getScore());
        exchange.setCreateTime(DateUtil.now());
        exchange.setStatus("待处理");

        goodsExchangeService.save(exchange);

        return Result.success();
    }
}