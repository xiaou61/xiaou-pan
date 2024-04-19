package com.xiaou.pan.server.modules.file.po;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 文件上传PO
 */
@Data
public class FileUploadPo implements Serializable {

    private static final long serialVersionUID = -8549029527243992293L;

    @NotBlank(message = "文件名不能为空")
    private String filename;

    @NotBlank(message = "文件唯一标识不能为空")
    private String identifier;

    @NotNull(message = "文件大小不能为空")
    private Long totalSize;

    @NotBlank(message = "文件父文件ID不能为空")
    private String parentId;

    @NotNull(message = "文件实体不能为空")
    private MultipartFile file;
}
