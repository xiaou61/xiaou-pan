package com.xiaou.pan.server.modules.file.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文件删除标识枚举类
 */
@AllArgsConstructor
@Getter
public enum DelFlagEnum {

    /**
     * 未删除
     */
    NOT_DELETE(0),

    /**
     * 已删除
     */
    DELETED(1);
    private final Integer code;
}
