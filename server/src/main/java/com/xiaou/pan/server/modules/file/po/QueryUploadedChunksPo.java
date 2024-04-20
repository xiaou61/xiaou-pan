package com.xiaou.pan.server.modules.file.po;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class QueryUploadedChunksPo implements Serializable {

    private static final long serialVersionUID = 7398535711895643520L;

    /**
     * 文件的唯一标识
     */
    @NotBlank(message = "文件唯一标识不能为空")
    private String identifier;

}
