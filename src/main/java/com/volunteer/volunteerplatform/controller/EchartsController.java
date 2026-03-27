package com.volunteer.volunteerplatform.controller;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.volunteer.volunteerplatform.common.Result;
import com.volunteer.volunteerplatform.entity.Activity;
import com.volunteer.volunteerplatform.entity.ActivitySignup;
import com.volunteer.volunteerplatform.service.IActivityService;
import com.volunteer.volunteerplatform.service.IActivitySignupService;
import com.volunteer.volunteerplatform.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/echarts")
public class EchartsController {

    @Autowired
    private IUserService userService;
    @Autowired
    private IActivityService activityService;
    @Autowired
    private IActivitySignupService signupService;

    // 1. 获取顶部四个卡片的总计数据
    @GetMapping("/total")
    public Result getTotal() {
        Map<String, Object> map = new HashMap<>();
        map.put("userTotal", userService.count()); // 总志愿者
        map.put("activityTotal", activityService.count()); // 志愿活动总数
        map.put("signupTotal", signupService.count()); // 累计报名人次

        // 已完成的报名人次 * 4小时 = 累计服务时长
        QueryWrapper<ActivitySignup> completedQuery = new QueryWrapper<>();
        completedQuery.eq("status", "已完成");
        map.put("timeTotal", signupService.count(completedQuery) * 4);

        return Result.success(map);
    }

    // 2. 获取近七日报名趋势数据（真实按日期聚合查询）
    @GetMapping("/trend")
    public Result getTrend() {
        List<String> dateList = new ArrayList<>();
        List<Integer> countList = new ArrayList<>();

        // 获取最近7天的日期及每天的报名数
        for (int i = 6; i >= 0; i--) {
            Date date = DateUtil.offsetDay(new Date(), -i);
            String dateStr = DateUtil.formatDate(date);
            dateList.add(dateStr);

            // 查询当天报名记录数：signup_time 以该日期开头
            QueryWrapper<ActivitySignup> query = new QueryWrapper<>();
            query.like("signup_time", dateStr);
            long count = signupService.count(query);
            countList.add((int) count);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("dates", dateList);
        map.put("counts", countList);
        return Result.success(map);
    }

    // 3. 获取活动类型占比分布
    @GetMapping("/type")
    public Result getTypeDistribution() {
        List<Activity> list = activityService.list();

        // 按类型分组并计数，过滤掉没有类型的脏数据
        Map<String, Long> collect = list.stream()
                .filter(a -> a.getType() != null && !a.getType().trim().isEmpty())
                .collect(Collectors.groupingBy(Activity::getType, Collectors.counting()));

        List<Map<String, Object>> resultList = new ArrayList<>();
        for (String key : collect.keySet()) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", key);
            map.put("value", collect.get(key));
            resultList.add(map);
        }
        return Result.success(resultList);
    }
}