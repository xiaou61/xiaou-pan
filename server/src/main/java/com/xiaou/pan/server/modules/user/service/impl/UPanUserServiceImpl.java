package com.xiaou.pan.server.modules.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaou.pan.server.modules.user.domain.UPanUser;
import com.xiaou.pan.server.modules.user.service.UPanUserService;
import com.xiaou.pan.server.modules.user.mapper.UPanUserMapper;
import org.springframework.stereotype.Service;

/**
* @author Lenovo
* @description 针对表【u_pan_user(用户信息表)】的数据库操作Service实现
* @createDate 2024-04-13 15:24:24
*/
@Service
public class UPanUserServiceImpl extends ServiceImpl<UPanUserMapper, UPanUser>
    implements UPanUserService{

}




