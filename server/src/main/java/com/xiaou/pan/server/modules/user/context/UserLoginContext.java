package com.xiaou.pan.server.modules.user.context;

import com.xiaou.pan.server.modules.user.domain.UPanUser;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册业务的上下文实体对象
 */
@Data
public class UserLoginContext implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 用户实体对象
     */
    private UPanUser entity;

    /**
     * 登陆成功之后的凭证
     */
    private String accessToken;
}
