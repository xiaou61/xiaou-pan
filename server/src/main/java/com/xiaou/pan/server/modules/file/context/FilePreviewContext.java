package com.xiaou.pan.server.modules.file.context;

import lombok.Data;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
@Data
public class FilePreviewContext implements Serializable {

    private static final long serialVersionUID = -7611249063907061674L;

    private Long fileId;

    private HttpServletResponse response;

    private Long userId;
}
