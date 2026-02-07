package com.volunteer.volunteerplatform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.volunteer.volunteerplatform.entity.Activity;
import com.volunteer.volunteerplatform.mapper.ActivityMapper;
import com.volunteer.volunteerplatform.service.IActivityService;
import org.springframework.stereotype.Service;

@Service
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements IActivityService {
}