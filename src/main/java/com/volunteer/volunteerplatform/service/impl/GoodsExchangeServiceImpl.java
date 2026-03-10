package com.volunteer.volunteerplatform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.volunteer.volunteerplatform.entity.GoodsExchange;
import com.volunteer.volunteerplatform.mapper.GoodsExchangeMapper;
import com.volunteer.volunteerplatform.service.IGoodsExchangeService;
import org.springframework.stereotype.Service;

@Service
public class GoodsExchangeServiceImpl extends ServiceImpl<GoodsExchangeMapper, GoodsExchange> implements IGoodsExchangeService {
}