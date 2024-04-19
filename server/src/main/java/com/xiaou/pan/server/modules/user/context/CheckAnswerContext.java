package com.xiaou.pan.server.modules.user.context;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 校验密保答案Context对象
 */
@Data
public class CheckAnswerContext implements Serializable {

    private static final long serialVersionUID = -7402936584714720297L;

    private String username;

    private String question;


    private String answer;
}
