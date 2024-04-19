package com.xiaou.pan.server.modules.file.po;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 文件妙传参数实体
 */
@Data
public class SecUploadFilePo implements Serializable {

    private static final long serialVersionUID = -8224503349764344910L;

    @NotBlank(message = "文件夹id不能为空")
    private String parentId;

    @NotBlank(message = "文件名不能为空")
    private String filename;

    /**
     * 唯一标识
     */
    @NotBlank(message = "文件唯一标识不能为空")
    private String identifier;

}
