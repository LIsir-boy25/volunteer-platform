package com.volunteer.volunteerplatform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("goods_exchange")
public class GoodsExchange {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private Integer goodsId;
    private String goodsName;
    private String goodsImg;
    private Integer score;
    private String createTime;
    private String status;
    private String phone;
    private String address;

    // 【核心新增】：用于给前端传递兑换人姓名，但告诉MyBatis不要去数据库表里找这个字段
    @TableField(exist = false)
    private String userName;
}