package com.xiaou.pan.server.modules.user.constants;

/**
 * 用户模块的常量类
 */
public interface UserConstants {
    /**
     * 登陆用户的用户id的key值
     */
    String LOGIN_USER_ID = "LOGINUSERID";

    /**
     * 用户忘记密码-重置密码临时token的可以
     */
    String FORGET_USER_NAME = "FORGETUSERNAME";

    /**
     * 一天的毫秒值
     */
    Long ONE_DAY_LONG = 24L * 60L * 60L * 1000L;

    /**
     * 用户登陆缓存前缀
     */
    String USER_LOGIN_PREFIX = "USER_LOGIN_";

    /**
     * 五分钟
     */
    Long FIVE_MINUTES_LONG = 5L * 60L * 1000L;
}
