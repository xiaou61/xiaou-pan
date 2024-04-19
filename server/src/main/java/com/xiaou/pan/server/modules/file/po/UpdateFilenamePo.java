package com.xiaou.pan.server.modules.file.po;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 文件重命名
 */
@Data

public class UpdateFilenamePo implements Serializable {

    private static final long serialVersionUID = -6042289719050480465L;

    @NotBlank(message = "文件id不能为空")
    private String fileId;

    @NotBlank(message = "新文件名不能为空")
    private String newFilename;

}
