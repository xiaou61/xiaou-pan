package com.xiaou.pan.server.modules.user.po;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 用户登陆参数实体对象
 */
@Data
public class UserLoginPo implements Serializable {
    private static final long serialVersionUID = 1L;
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_-]{4,16}$", message = "用户名格式错误")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Length(min = 6, max = 20, message = "密码长度必须在6到20之间")
    private String password;
}
