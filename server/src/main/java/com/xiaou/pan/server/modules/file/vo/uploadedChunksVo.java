package com.xiaou.pan.server.modules.file.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 查询用户已上传的文件分片列表返回实体
 */
@Data
public class uploadedChunksVo implements Serializable {

    private static final long serialVersionUID = -3956205113803219392L;

    /**
     * 已上传的分片编号列表
     */
    private List<Integer> uploadedChunks;
}
