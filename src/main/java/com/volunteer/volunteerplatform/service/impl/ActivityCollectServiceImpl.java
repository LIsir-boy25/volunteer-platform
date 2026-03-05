package com.volunteer.volunteerplatform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.volunteer.volunteerplatform.entity.ActivityCollect;
import com.volunteer.volunteerplatform.mapper.ActivityCollectMapper;
import com.volunteer.volunteerplatform.service.IActivityCollectService;
import org.springframework.stereotype.Service;

@Service
public class ActivityCollectServiceImpl extends ServiceImpl<ActivityCollectMapper, ActivityCollect> implements IActivityCollectService {
}