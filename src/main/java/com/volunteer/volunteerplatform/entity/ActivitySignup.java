package com.volunteer.volunteerplatform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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

    private String activityName;
    private String nickname;
    private String studentId;

    private String signupTime;
    private String status;

    /**
     * 🌟 核心补充：用于前端判断“发布心得”还是“我的心得”按钮 🌟
     * 使用 @TableField(exist = false) 注解
     * 作用：告诉 MyBatis-Plus 数据库表里没这一列，不要去数据库里查它，仅用于代码逻辑传递。
     */
    @TableField(exist = false)
    private Boolean hasReview;
}