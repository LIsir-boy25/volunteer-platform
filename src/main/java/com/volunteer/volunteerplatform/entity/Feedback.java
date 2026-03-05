package com.volunteer.volunteerplatform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField; // 【必须引入】：处理数据库不存在的字段
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("feedback")
public class Feedback {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private String username;
    private String content;
    private String reply;
    private String createTime;

    // 【核心修复】：数据库不存在的字段，专门用来装载 Controller 查出的真实姓名并发给前端
    @TableField(exist = false)
    private String nickname;
}