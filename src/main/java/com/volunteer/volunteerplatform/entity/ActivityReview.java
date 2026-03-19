package com.volunteer.volunteerplatform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("activity_review")
public class ActivityReview {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private Integer activityId;
    private String content;
    private String createTime;

    // 数据库不存在的字段，用来给前端显示名字
    @TableField(exist = false)
    private String nickname;
    @TableField(exist = false)
    private String activityName;

    // 【核心新增】：数据库不存在的字段，用来给前端显示头像
    @TableField(exist = false)
    private String avatar;
}