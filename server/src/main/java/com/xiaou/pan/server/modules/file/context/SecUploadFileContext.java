package com.xiaou.pan.server.modules.file.context;

import lombok.Data;

import java.io.Serializable;

/**
 * 文件妙传上下文实体对象
 */
@Data
public class SecUploadFileContext implements Serializable {

    private static final long serialVersionUID = 2760507855520535263L;

    private Long parentId;

    private String filename;

    private String identifier;

    private Long userId;
}
