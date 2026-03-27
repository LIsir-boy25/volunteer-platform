package com.volunteer.volunteerplatform.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.volunteer.volunteerplatform.common.Result;
import com.volunteer.volunteerplatform.entity.ActivitySignup;
import com.volunteer.volunteerplatform.service.IActivityService;
import com.volunteer.volunteerplatform.service.IActivitySignupService;
import com.volunteer.volunteerplatform.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IActivityService activityService;

    @Autowired
    private IActivitySignupService activitySignupService;

    /**
     * 获取首页统计数据（真实数据库查询）
     */
    @GetMapping("/stats")
    public Result getStats() {
        Map<String, Object> map = new HashMap<>();

        // 1. 总用户数
        map.put("userCount", userService.count());

        // 2. 总活动数
        map.put("activityCount", activityService.count());

        // 3. 累计报名人次
        map.put("signupCount", activitySignupService.count());

        // 4. 已完成活动人次（作为工时结算依据，每次完成计4小时）
        QueryWrapper<ActivitySignup> completedQuery = new QueryWrapper<>();
        completedQuery.eq("status", "已完成");
        long completedCount = activitySignupService.count(completedQuery);
        map.put("totalHours", completedCount * 4);

        return Result.success(map);
    }
}