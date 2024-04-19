package com.xiaou.pan.server.modules.file.po;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 创建文件夹参数实体
 */
@Data
public class CreateFolderPo implements Serializable {

    private static final long serialVersionUID = 523693984133807394L;

    @NotBlank(message = "父文件夹id不能为空")
    private String parentId;
    @NotBlank(message = "文件夹名称不能为空")
    private String folderName;

}
