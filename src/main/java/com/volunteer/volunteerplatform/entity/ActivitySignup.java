package com.volunteer.volunteerplatform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("activity_signup")
public class ActivitySignup {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer userId;
    private Integer activityId;

    // 现在数据库有这些字段了，不需要加 @TableField(exist = false)
    private String activityName;
    private String nickname;
    private String studentId;

    private String signupTime;
    private String status;
}