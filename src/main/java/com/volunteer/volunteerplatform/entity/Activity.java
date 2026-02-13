package com.volunteer.volunteerplatform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("activity")
public class Activity {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;       // 活动名称
    private String type;       // 活动类型
    private String content;    // 活动内容
    private String startTime;  // 开始时间
    private String endTime;    // 结束时间
    private String location;   // 活动地点
}