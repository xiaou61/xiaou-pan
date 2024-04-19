package com.xiaou.pan.server.modules.user.po;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 校验密保答案Po对象
 */
@Data
public class CheckAnswerPo implements Serializable {

    private static final long serialVersionUID = 4757094172033714038L;
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_-]{4,16}$", message = "用户名格式错误")
    private String username;

    @NotBlank(message = "密保问题不能为空")
    @Length(min = 1, max = 50, message = "密保问题长度必须在1到50之间")
    private String question;

    @NotBlank(message = "密保答案不能为空")
    @Length(min = 1, max = 50, message = "密保答案长度必须在1到50之间")
    private String answer;
}
