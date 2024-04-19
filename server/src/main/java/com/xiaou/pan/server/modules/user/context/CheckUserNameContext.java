package com.xiaou.pan.server.modules.user.context;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 校验用户名称context
 */
@Data

public class CheckUserNameContext implements Serializable {

    private static final long serialVersionUID = 1290390883211670571L;
    private String username;
}
