package com.xiaou.pan.server.modules.file.service;

import com.xiaou.pan.server.modules.file.context.*;
import com.xiaou.pan.server.modules.file.context.FileUploadContext;
import com.xiaou.pan.server.modules.file.domain.UPanUserFile;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaou.pan.server.modules.file.vo.FileChunkUploadVO;
import com.xiaou.pan.server.modules.file.vo.UPanUserFileVO;
import com.xiaou.pan.server.modules.file.vo.uploadedChunksVo;

import java.util.List;

/**
* @author Lenovo
* @description 针对表【u_pan_user_file(用户文件信息表)】的数据库操作Service
* @createDate 2024-04-13 15:26:52
*/
public interface IUserFileService extends IService<UPanUserFile> {
    /**
     * 创建文件夹信息
     * @param createFolderContext
     * @return
     */
    Long createFolder(CreateFolderContext createFolderContext);

    /**
     * 查询用户的根文件夹信息
     * @param userId
     * @return
     */
    UPanUserFile getUserRootFile(Long userId);

    /**
     * 查询用户的文件列表
     * @param context
     * @return
     */
    List<UPanUserFileVO> getFileList(QueryFileListContext context);

    /**
     * 重命名文件名
     * @param context
     */
    void updateFilename(UpdateFilenameContext context);

    /**
     * 批量删除用户文件
     * @param context
     */
    void deleteFile(DeleteFileContext context);

    /**
     * 文件妙传
     * @param context
     * @return
     */
    boolean setUpload(SecUploadFileContext context);

    /**
     * 文件上传
     *
     * @param context
     */
    void upload(FileUploadContext context);

    /**
     * 文件分片上传
     * @param context
     * @return
     */
    FileChunkUploadVO chunkUpload(FileChunkUploadContext context);

    /**
     * 查询用户已上传的分片列表
     * @param context
     * @return
     */
    uploadedChunksVo getUploadedChunks(UploadedChunksContext context);

    /**
     * 文件分片合并
     * @param context
     */
    void mergeFile(FileChunkMergeContext context);

    /**
     * 文件下载
     * @param context
     */
    void download(FileDownloadContext context);
}
