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
    private String name;
    private String type;
    private String content;
    private String startTime;  // 开始时间
    private String endTime;    // 结束时间
    private String location;
    private String img;
    private Integer maxPeople;
    private Integer score;
    private String status;
}