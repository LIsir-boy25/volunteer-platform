package com.volunteer.volunteerplatform.service.impl;

import com.volunteer.volunteerplatform.entity.ActivityType;
import com.volunteer.volunteerplatform.mapper.ActivityTypeMapper;
import com.volunteer.volunteerplatform.service.IActivityTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ActivityTypeServiceImpl extends ServiceImpl<ActivityTypeMapper, ActivityType> implements IActivityTypeService {
}