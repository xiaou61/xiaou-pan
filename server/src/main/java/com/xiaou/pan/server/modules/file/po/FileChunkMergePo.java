package com.xiaou.pan.server.modules.file.po;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class FileChunkMergePo implements Serializable {

    private static final long serialVersionUID = 6797116793227102208L;

    @NotBlank(message = "文件名不能为空")
    private String filename;

    @NotBlank(message = "文件唯一标识不能为空")
    private String identifier;

    @NotNull(message = "文件总大小不能为空")
    private Long totalSize;

    @NotBlank(message = "文件父级id不能为空")
    private String parentId;


}
