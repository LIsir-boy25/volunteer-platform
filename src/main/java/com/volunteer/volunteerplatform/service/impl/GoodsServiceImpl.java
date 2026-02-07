package com.volunteer.volunteerplatform.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.volunteer.volunteerplatform.entity.Goods;
import com.volunteer.volunteerplatform.mapper.GoodsMapper;
import com.volunteer.volunteerplatform.service.IGoodsService;
import org.springframework.stereotype.Service;

@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements IGoodsService {
}