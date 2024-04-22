package com.xiaou.pan.server.modules.file.context;

import lombok.Data;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

/**
 * 文件下载的上下文实体对象
 */
@Data
public class FileDownloadContext implements Serializable {

    private static final long serialVersionUID = -8152149712381392067L;

    private Long fileId;

    private HttpServletResponse response;

    private Long userId;
}
