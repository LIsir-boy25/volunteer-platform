package com.volunteer.volunteerplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.volunteer.volunteerplatform.entity.SysUser;
import com.volunteer.volunteerplatform.mapper.UserMapper;
import com.volunteer.volunteerplatform.service.IUserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, SysUser> implements IUserService {

    @Override
    public SysUser login(String username, String password) {
        // 1. 查询数据库是否存在该用户名
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        SysUser user = getOne(wrapper);

        // 2. 如果没查到
        if (user == null) {
            throw new RuntimeException("账号不存在");
        }

        // 3. 比对密码
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("密码错误");
        }

        // 4. 返回用户信息
        return user;
    }
}
