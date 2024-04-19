package com.xiaou.pan.server.modules.user.po;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 在线修改密码Po对象
 */
@Data
public class ChangePasswordPo implements Serializable {


    private static final long serialVersionUID = 6169925290644601456L;
    @NotBlank(message = "密码不能为空")
    @Length(min = 6, max = 20, message = "密码长度必须在6到20之间")
    private String oldPassword;

    @NotBlank(message = "密码不能为空")
    @Length(min = 6, max = 20, message = "密码长度必须在6到20之间")
    private String newPassword;
}
