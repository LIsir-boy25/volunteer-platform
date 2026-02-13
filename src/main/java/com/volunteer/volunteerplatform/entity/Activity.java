package com.volunteer.volunteerplatform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("activity")
public class Activity {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;
    private String type;
    private String content;

    // ✅ 对应数据库的 time 字段
    private String time;

    // ✅ 对应数据库的 signup_time 字段 (自动驼峰转换)
    private String signupTime;

    // ✅ 对应数据库的 address 字段
    private String address;

    // ✅ 对应数据库的 num 字段
    private Integer num;

    // ✅ 对应数据库的 credit 字段
    private Integer credit;

    private String status;

    // ✅ 必须保留，防止后端报 500 错误
    @TableLogic
    private Integer deleted;
}