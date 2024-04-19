package com.xiaou.pan.server.common.annotation;

import java.lang.annotation.*;

/**
 * 该注解主要影响不需要登陆的接口
 * 会自动屏蔽统一的登陆拦截器校验
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginIgnore {
}
