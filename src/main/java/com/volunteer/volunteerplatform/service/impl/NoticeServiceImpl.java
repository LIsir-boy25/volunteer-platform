package com.volunteer.volunteerplatform.service.impl;

import com.volunteer.volunteerplatform.entity.Notice;
import com.volunteer.volunteerplatform.mapper.NoticeMapper;
import com.volunteer.volunteerplatform.service.INoticeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements INoticeService {
}