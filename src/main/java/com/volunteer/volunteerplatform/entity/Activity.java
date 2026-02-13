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

    // ✅ 改名：对应前端的 time
    private String time;

    // ✅ 改名：对应前端的 signupTime (数据库 signup_time)
    private String signupTime;

    // ✅ 改名：对应前端的 address
    private String address;

    // ✅ 改名：对应前端的 num (数据库 num)
    private Integer num;

    // ✅ 改名：对应前端的 credit (数据库 credit)
    private Integer credit;

    private String status;

    // ✅ 保留此字段：虽然 yml 里删了全局配置，但加上这个注解可以单独为 Activity 表开启逻辑删除
    // 只要你的数据库 activity 表里有 deleted 字段，这就不会报错
    @TableLogic
    private Integer deleted;
}