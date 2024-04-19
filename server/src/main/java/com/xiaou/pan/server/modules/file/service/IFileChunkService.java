package com.xiaou.pan.server.modules.file.service;

import com.xiaou.pan.server.modules.file.context.FileChunkSaveContext;
import com.xiaou.pan.server.modules.file.domain.UPanFileChunk;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Lenovo
* @description 针对表【u_pan_file_chunk(文件分片信息表)】的数据库操作Service
* @createDate 2024-04-13 15:26:52
*/
public interface IFileChunkService extends IService<UPanFileChunk> {

    /**
     * 文件分片上传保存
     * @param fileChunkSaveContext
     */
    void saveChunkFile(FileChunkSaveContext fileChunkSaveContext);
}
