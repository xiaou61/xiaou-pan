package com.xiaou.pan.server.modules.file.context;

import lombok.Data;

import java.io.Serializable;

/**
 * 文件分片合并的上下文实体对象
 */
@Data
public class FileChunkMergeContext implements Serializable {

    private static final long serialVersionUID = -7971458041927394757L;

    private String filename;

    private String identifier;

    private Long totalSize;

    private String parentId;

    private Long userId;
}
