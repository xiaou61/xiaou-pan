package com.xiaou.pan.server.modules.file.po;

import com.xiaou.pan.core.response.R;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class FileChunkUploadPo implements Serializable {

    private static final long serialVersionUID = 3741130065248789701L;

    @NotBlank(message = "文件名不能为空")
    private String filename;

    @NotBlank(message = "文件标识不能为空")
    private String identifier;

    @NotNull(message = "文件分片数不能为空")
    private Integer totalChunks;


    @NotNull(message = "文件分片数不能为空")
    private Integer chunkNumber;

    @NotNull(message = "文件分片大小不能为空")
    private Long currentChunkSize;

    @NotNull(message = "文件总大小不能为空")
    private Long totalSize;

    @NotNull(message = "文件不能为空")
    private MultipartFile file;

}
