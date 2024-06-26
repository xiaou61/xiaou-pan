package com.xiaou.pan.server.modules.file.context;

import com.xiaou.pan.server.modules.file.domain.UPanFile;
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

    private Long parentId;

    private Long userId;

    /**
     * 物理文件记录
     *
     */
    private UPanFile record;
}
