package com.volunteer.volunteerplatform.service;

import com.volunteer.volunteerplatform.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IUserService extends IService<SysUser> {

    //在此处添加 login 方法的定义
    SysUser login(String username, String password);
}