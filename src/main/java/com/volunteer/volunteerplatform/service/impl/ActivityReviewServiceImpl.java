package com.volunteer.volunteerplatform.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.volunteer.volunteerplatform.entity.ActivityReview;
import com.volunteer.volunteerplatform.mapper.ActivityReviewMapper;
import com.volunteer.volunteerplatform.service.IActivityReviewService;
import org.springframework.stereotype.Service;

@Service
public class ActivityReviewServiceImpl extends ServiceImpl<ActivityReviewMapper, ActivityReview> implements IActivityReviewService {
}