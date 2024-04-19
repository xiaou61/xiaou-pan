package com.xiaou.pan.server.modules.file.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.xiaou.pan.web.serializer.IdEncryptSerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户查询文件列表响应实体
 */
@Data
public class UPanUserFileVO implements Serializable {

    private static final long serialVersionUID = -2925667883005045853L;

    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long fileId;

    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long parentId;

    private String filename;

    private String fileSizeDesc;

    private Integer folderFlag;

    private Integer fileType;

    private Date updateTime;
}
