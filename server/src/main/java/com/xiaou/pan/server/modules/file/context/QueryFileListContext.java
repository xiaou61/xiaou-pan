package com.xiaou.pan.server.modules.file.context;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 查询文件列表上下文实体
 */
@Data
public class QueryFileListContext implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 父文件夹ID
     */

    private Long parentId;

    /**
     * 文件类型
     */
    private List<Integer> fileTypeArray;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 删除标记
     */

    private Integer delFlag;
}
