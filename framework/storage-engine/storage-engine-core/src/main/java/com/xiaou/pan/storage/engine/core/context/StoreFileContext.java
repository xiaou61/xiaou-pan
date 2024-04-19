package com.xiaou.pan.storage.engine.core.context;

import lombok.Data;

import java.io.InputStream;
import java.io.Serializable;

/**
 * 文件存储引擎，存储物理文件的上下文实体
 */
@Data
public class StoreFileContext implements Serializable {

    private static final long serialVersionUID = 2845847874680995353L;

    private String filename;

    private long totalSize;

    private InputStream inputStream;

    private String realPath;
}
