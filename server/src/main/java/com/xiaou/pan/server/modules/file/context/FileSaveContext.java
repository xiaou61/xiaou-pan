package com.xiaou.pan.server.modules.file.context;

import com.xiaou.pan.server.modules.file.domain.UPanFile;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * 保存单文件的上下文实体
 */
@Data
public class FileSaveContext implements Serializable {

    private static final long serialVersionUID = -515994250856109109L;

    private String filename;

    private String identifier;

    private Long totalSize;

    private MultipartFile file;

    private Long userId;

    private UPanFile record;

    /**
     * 文件上传的物理路径
     */
    private String realPath;
}
