package com.volunteer.volunteerplatform.controller;

import com.volunteer.volunteerplatform.common.Result;
import com.volunteer.volunteerplatform.mapper.ActivityMapper;
import com.volunteer.volunteerplatform.mapper.ActivitySignupMapper;
import com.volunteer.volunteerplatform.mapper.UserMapper;
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
    private UserMapper userMapper;

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private ActivitySignupMapper activitySignupMapper;

    /**
     * 获取首页统计数据
     */
    @GetMapping("/stats")
    public Result getStats() {
        Map<String, Object> map = new HashMap<>();

        // 1. 总用户数 (需要你在 UserMapper XML或注解里写好 count 方法)
        // long userCount = userMapper.selectCount(null);
        // 暂时用 Mybatis-Plus 的 selectCount

        // 2. 总活动数
        // long activityCount = activityMapper.selectCount(null);

        // 3. 模拟返回数据 (等你把 Mapper 写完善了再换成查数据库)
        map.put("userCount", 108);      // 替换为 userMapper.selectCount(null)
        map.put("activityCount", 12);   // 替换为 activityMapper.selectCount(null)
        map.put("signupCount", 320);    // 替换为 activitySignupMapper.selectCount(null)
        map.put("totalHours", 560);     // 需要写 SQL: select sum(duration) from activity_signup

        return Result.success(map);
    }
}