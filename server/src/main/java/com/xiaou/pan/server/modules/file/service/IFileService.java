package com.xiaou.pan.server.modules.file.service;

import com.xiaou.pan.server.modules.file.context.FileChunkMergeAndSaveContext;
import com.xiaou.pan.server.modules.file.context.FileChunkMergeContext;
import com.xiaou.pan.server.modules.file.context.FileSaveContext;
import com.xiaou.pan.server.modules.file.domain.UPanFile;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author Lenovo
 * @description 针对表【u_pan_file(物理文件信息表)】的数据库操作Service
 * @createDate 2024-04-13 15:26:52
 */
public interface IFileService extends IService<UPanFile> {
    /**
     * 上传单文件并保存实体记录
     *
     * @param context
     */
    void saveFile(FileSaveContext context);

    /**
     * 合并文件分片并保存实体记录
     *
     * @param fileChunkMergeContext
     */
    void mergeFileChunkAndSave(FileChunkMergeAndSaveContext fileChunkMergeContext);
}
