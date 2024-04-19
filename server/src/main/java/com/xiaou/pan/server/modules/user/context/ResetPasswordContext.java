package com.xiaou.pan.server.modules.user.context;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 重置用户密码po
 */
@Data
public class ResetPasswordContext implements Serializable {


    private static final long serialVersionUID = 6756867139866823609L;
    private String username;

    private String password;

    private String token;
}
