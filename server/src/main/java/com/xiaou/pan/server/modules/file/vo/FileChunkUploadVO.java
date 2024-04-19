package com.xiaou.pan.server.modules.file.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class FileChunkUploadVO implements Serializable {

    private static final long serialVersionUID = 1650797587754096429L;

    /**
     * 是否需要合并文件 0不需要 1需要
     */
    private Integer mergeFlag;
}
