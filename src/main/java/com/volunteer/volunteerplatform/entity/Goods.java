package com.volunteer.volunteerplatform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("goods")
public class Goods {
    @TableId(type = IdType.AUTO)
    private Integer id;

    // 商品名称
    private String name;

    // 商品所需积分
    private Integer score;

    // 商品库存
    private Integer store;

    // 商品图片
    private String img;

    // 创建/上架时间
    private String createTime;

    // 【核心新增】商品状态，用于控制前台的展示（上架 / 下架）
    private String status;
}