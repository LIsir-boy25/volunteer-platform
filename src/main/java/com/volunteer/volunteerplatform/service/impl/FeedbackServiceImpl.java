package com.volunteer.volunteerplatform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.volunteer.volunteerplatform.entity.Feedback;
import com.volunteer.volunteerplatform.mapper.FeedbackMapper;
import com.volunteer.volunteerplatform.service.IFeedbackService;
import org.springframework.stereotype.Service;

@Service
public class FeedbackServiceImpl extends ServiceImpl<FeedbackMapper, Feedback> implements IFeedbackService {
}