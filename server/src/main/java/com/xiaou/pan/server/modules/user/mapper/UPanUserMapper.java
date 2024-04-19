package com.xiaou.pan.server.modules.user.mapper;

import com.xiaou.pan.server.modules.user.domain.UPanUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author Lenovo
 * @description 针对表【u_pan_user(用户信息表)】的数据库操作Mapper
 * @createDate 2024-04-13 15:24:24
 * @Entity com.xiaou.pan.server.modules.user.domain.UPanUser
 */
public interface UPanUserMapper extends BaseMapper<UPanUser> {

    /**
     * 通过用户名称查询用户设置到密保问题
     *
     * @param username
     * @return
     */
    String selectQuestionByUsername(@Param("username") String username);
}




