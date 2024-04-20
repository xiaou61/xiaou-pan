package com.xiaou.pan.server.modules.file.converter;


import com.xiaou.pan.server.modules.file.context.*;
import com.xiaou.pan.server.modules.file.context.FileUploadContext;
import com.xiaou.pan.server.modules.file.po.*;
import com.xiaou.pan.storage.engine.core.context.StoreFileChunkContext;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 文件模块实体转换工具类
 */
@Mapper(componentModel = "spring")
public interface FileConverter {

    @Mapping(target = "userId", expression = "java(com.xiaou.pan.server.common.utils.UserIdUtil.get())")
    CreateFolderContext CreateFolderPo2CreateFolderContext(CreateFolderPo createFolderPo);

    @Mapping(target = "userId", expression = "java(com.xiaou.pan.server.common.utils.UserIdUtil.get())")
    UpdateFilenameContext UpdateFilenamePo2UpdateFilenameContext(UpdateFilenamePo updateFilenamePo);

    @Mapping(target = "userId", expression = "java(com.xiaou.pan.server.common.utils.UserIdUtil.get())")
    DeleteFileContext DeleteFilePo2DeleteFileContext(DeleteFilePo deleteFilePo);

    @Mapping(target = "userId", expression = "java(com.xiaou.pan.server.common.utils.UserIdUtil.get())")
    SecUploadFileContext SecUploadFilePo2SecUploadFileContext(SecUploadFilePo secUploadFilePo);

    @Mapping(target = "userId", expression = "java(com.xiaou.pan.server.common.utils.UserIdUtil.get())")
    FileUploadContext FileUploadPo2FileUploadContext(FileUploadPo fileUploadPo);

    @Mapping(target = "record", ignore = true)
    FileSaveContext fileUploadContext2FileSaveContext(FileUploadContext context);

    @Mapping(target = "userId", expression = "java(com.xiaou.pan.server.common.utils.UserIdUtil.get())")
    FileChunkUploadContext FileChunkUploadPo2FileChunkUploadContext(FileChunkUploadPo fileChunkUploadPo);

    FileChunkSaveContext FileChunkUploadContext2FileChunkSaveContext(FileChunkUploadContext context);

    @Mapping(target = "realPath", ignore = true)
    StoreFileChunkContext fileChunkSaveContext2StoreFileChunkContext(FileChunkSaveContext context);

    @Mapping(target = "userId", expression = "java(com.xiaou.pan.server.common.utils.UserIdUtil.get())")
    UploadedChunksContext QueryUploadedChunksPo2uploadedChunksContext(QueryUploadedChunksPo queryUploadedChunksPo);

    @Mapping(target = "userId", expression = "java(com.xiaou.pan.server.common.utils.UserIdUtil.get())")
    FileChunkMergeContext FileChunkMergePo2FileChunkMergeContext(FileChunkMergePo fileChunkMergePo);
}
