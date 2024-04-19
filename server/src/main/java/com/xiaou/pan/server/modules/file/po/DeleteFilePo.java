package com.xiaou.pan.server.modules.file.po;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 删除文件po
 */
@Data
public class DeleteFilePo implements Serializable {

    private static final long serialVersionUID = 6331042996374678044L;

    @NotBlank(message = "文件id不能为空")
    private String fileIds;
}
