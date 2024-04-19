package com.xiaou.pan.server.modules.file.context;

import com.xiaou.pan.server.modules.file.enums.MergeFlagEnum;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Data
public class FileChunkSaveContext implements Serializable {


    private static final long serialVersionUID = -6363650745328616534L;

    private String filename;

    private String identifier;

    private Integer totalChunks;


    private Integer chunkNumber;

    private Long currentChunkSize;

    private Long totalSize;

    private MultipartFile file;

    private Long userId;

    /**
     * 文件合并标识。
     */
    private MergeFlagEnum mergeFlagEnum = MergeFlagEnum.NOT_READY;

    private String realPath;
}
