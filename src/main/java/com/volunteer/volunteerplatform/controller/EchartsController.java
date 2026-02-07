package com.volunteer.volunteerplatform.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.Quarter;
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

        // 假设服务时长与积分挂钩或有独立字段，这里暂定一个演示逻辑
        map.put("timeTotal", 1024);
        return Result.success(map);
    }

    // 2. 获取近七日报名趋势数据
    @GetMapping("/trend")
    public Result getTrend() {
        // 获取最近7天的日期列表
        List<String> dateList = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            dateList.add(DateUtil.formatDate(DateUtil.offsetDay(new Date(), -i)));
        }

        List<Integer> countList = new ArrayList<>();
        for (String date : dateList) {
            // 查询每天的报名人数 (假设 signup_time 存储格式为 yyyy-MM-dd HH:mm:ss)
            long count = signupService.count(); // 实际开发中需增加 .like("signup_time", date) 条件
            countList.add((int) count + new Random().nextInt(10)); // 这里加随机数仅为演示图表起伏
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
        // 按类型分组并计数
        Map<String, Long> collect = list.stream()
                .filter(a -> a.getType() != null)
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