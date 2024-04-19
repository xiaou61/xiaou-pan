package com.xiaou.pan.server.modules.file.context;

import com.xiaou.pan.server.modules.file.domain.UPanUserFile;
import lombok.Data;

import java.io.Serializable;

/**
 * 更新文件名称context
 */
@Data
public class UpdateFilenameContext implements Serializable {

    private static final long serialVersionUID = 6066976698295076558L;
    private Long fileId;
    private String newFilename;
    private Long userId;
    private UPanUserFile entity;
}
