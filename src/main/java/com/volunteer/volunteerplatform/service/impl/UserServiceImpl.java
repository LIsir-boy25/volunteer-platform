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
        // 【核心优化】：强制只取第一条！彻底杜绝之前 TooManyResultsException 的报错隐患
        wrapper.last("limit 1");

        SysUser user = getOne(wrapper);

        // 2. 如果没查到该用户
        if (user == null) {
            throw new RuntimeException("账号不存在");
        }

        // 3. 比对密码 (增加防空判断，彻底避免空指针异常)
        // 注意：此时传进来的 password 已经是经过 UserController 加盐 MD5 处理后的 32 位密文了
        // 数据库里 user.getPassword() 取出的也是密文。密文比密文，安全又精准！
        if (user.getPassword() == null || !user.getPassword().equals(password)) {
            throw new RuntimeException("密码错误");
        }

        // 4. 返回用户信息
        return user;
    }
}