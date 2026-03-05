package com.volunteer.volunteerplatform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("activity_collect")
public class ActivityCollect {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private Integer activityId;
    private String time;

    // 下面这两个是用来给前端展示的虚拟字段（数据库没有）
    @TableField(exist = false)
    private String activityName;
    @TableField(exist = false)
    private String activityImg;
}