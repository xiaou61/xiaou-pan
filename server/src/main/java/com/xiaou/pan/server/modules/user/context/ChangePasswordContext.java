package com.xiaou.pan.server.modules.user.context;

import com.xiaou.pan.server.modules.user.domain.UPanUser;
import lombok.Data;

import java.io.Serializable;

/**
 * 在线修改用户密码context对象
 */
@Data
public class ChangePasswordContext implements Serializable {


    private static final long serialVersionUID = -5497506887913285471L;

    private Long userId;
    private String oldPassword;
    private String newPassword;
    /**
     * 当前登陆用户的实体信息
     */
    private UPanUser entity;
}
