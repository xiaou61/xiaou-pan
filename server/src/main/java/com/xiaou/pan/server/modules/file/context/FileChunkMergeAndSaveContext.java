package com.xiaou.pan.server.modules.file.context;

import com.xiaou.pan.server.modules.file.domain.UPanFile;
import lombok.Data;

import java.io.Serializable;

@Data
public class FileChunkMergeAndSaveContext implements Serializable {

    private static final long serialVersionUID = -7191699042515641372L;

    private String filename;

    private String identifier;

    private Long totalSize;

    private Long parentId;

    private Long userId;

    /**
     * 物理文件记录
     */
    private UPanFile record;

    /**
     * 文件合并的存储的真实的物理路径
     */
    private String realPath;
}
