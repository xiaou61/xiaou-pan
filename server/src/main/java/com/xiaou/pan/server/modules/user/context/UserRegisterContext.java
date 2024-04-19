package com.xiaou.pan.server.modules.user.context;

import com.xiaou.pan.server.modules.user.domain.UPanUser;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 用户注册业务的上下文实体对象
 */
@Data
public class UserRegisterContext implements Serializable {
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
     * 密保问题
     */
    private String question;

    /**
     * 答案
     */
    private String answer;

    /**
     * 用户实体对象
     */
    private UPanUser entity;
}
