package com.volunteer.volunteerplatform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("notice")
public class Notice {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String title;   // 标题
    private String content; // 内容

    // 【关键修改】这里必须是 String，才能配合 DateUtil.now()
    private String time;

    private String user;    // 发布人
}