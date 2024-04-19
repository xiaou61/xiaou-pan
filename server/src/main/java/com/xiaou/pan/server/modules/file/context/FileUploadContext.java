package com.xiaou.pan.server.modules.file.context;

import com.xiaou.pan.server.modules.file.domain.UPanFile;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * 单文件上传的上下文实体
 */
@Data
public class FileUploadContext implements Serializable {

    private static final long serialVersionUID = 8259197291681292378L;


    private String filename;


    private String identifier;

    private Long totalSize;


    private Long parentId;

    private MultipartFile file;

    private Long userId;

    /**
     * 实体文件记录
     */
    private UPanFile record;
}
