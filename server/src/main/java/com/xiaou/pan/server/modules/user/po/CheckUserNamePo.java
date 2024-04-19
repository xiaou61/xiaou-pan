package com.xiaou.pan.server.modules.user.po;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 校验用户名称po对象
 */
@Data

public class CheckUserNamePo implements Serializable {

    private static final long serialVersionUID = -2323262142282844467L;

    @NotBlank(message = "用户名称不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_-]{4,16}$", message = "用户名格式错误")
    private String username;
}
