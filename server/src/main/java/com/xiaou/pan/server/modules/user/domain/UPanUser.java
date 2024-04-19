package com.xiaou.pan.server.modules.user.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户信息表
 * @TableName u_pan_user
 */
@TableName(value ="u_pan_user")
@Data
public class UPanUser implements Serializable {
    /**
     * 用户id
     */
    @TableId(value = "user_id")
    private Long userId;

    /**
     * 用户名
     */
    @TableField(value = "username")
    private String username;

    /**
     * 密码
     */
    @TableField(value = "password")
    private String password;

    /**
     * 随机盐值
     */
    @TableField(value = "salt")
    private String salt;

    /**
     * 密保问题
     */
    @TableField(value = "question")
    private String question;

    /**
     * 密保答案
     */
    @TableField(value = "answer")
    private String answer;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}