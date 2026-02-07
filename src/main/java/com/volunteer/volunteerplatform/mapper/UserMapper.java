package com.volunteer.volunteerplatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.volunteer.volunteerplatform.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据访问层
 * 继承 BaseMapper 后，自动拥有 CRUD 能力
 */
@Mapper
public interface UserMapper extends BaseMapper<SysUser> {
}