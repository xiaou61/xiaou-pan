package com.xiaou.pan.server.modules.file.controller;

import com.google.common.base.Splitter;
import com.xiaou.pan.core.constants.RPanConstants;
import com.xiaou.pan.core.response.R;
import com.xiaou.pan.server.common.utils.UserIdUtil;
import com.xiaou.pan.server.modules.file.constants.FileConstants;
import com.xiaou.pan.server.modules.file.context.*;
import com.xiaou.pan.server.modules.file.converter.FileConverter;
import com.xiaou.pan.server.modules.file.enums.DelFlagEnum;
import com.xiaou.pan.server.modules.file.po.*;
import com.xiaou.pan.server.modules.file.service.IUserFileService;
import com.xiaou.pan.server.modules.file.vo.FileChunkUploadVO;
import com.xiaou.pan.server.modules.file.vo.UPanUserFileVO;
import com.xiaou.pan.server.modules.file.vo.uploadedChunksVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 文件模块管理器
 */
@RestController
@Validated
public class FileController {
    @Resource
    private IUserFileService userFileService;

    @Resource
    private FileConverter fileConverter;

    @GetMapping("files")
    public R<List<UPanUserFileVO>> list(@NotBlank(message = "父文件夹ID不能为空") @RequestParam(value = "parentId", required = false) String parentId,
                                        @RequestParam(value = "fileTypes", required = false, defaultValue = FileConstants.ALL_FILE_TYPE) String fileTypes) {

        List<Integer> fileTypeArray = null;


        if (!Objects.equals(FileConstants.ALL_FILE_TYPE, fileTypes)) {
            fileTypeArray = Splitter.on(RPanConstants.COMMON_SEPARATOR).splitToList(fileTypes).stream().map(Integer::valueOf).collect(Collectors.toList());
        }
        QueryFileListContext context = new QueryFileListContext();
        context.setParentId(Long.valueOf(parentId));
        context.setFileTypeArray(fileTypeArray);
        context.setDelFlag(DelFlagEnum.NOT_DELETE.getCode());
        context.setUserId(UserIdUtil.get());
        List<UPanUserFileVO> result = userFileService.getFileList(context);
        return R.data(result);
    }


    /**
     * 创建文件夹
     *
     * @param createFolderPo
     * @return
     */
    @PostMapping("file/folder")
    public R<String> createFolder(@Validated @RequestBody CreateFolderPo createFolderPo) {
        CreateFolderContext folderContext = fileConverter.CreateFolderPo2CreateFolderContext(createFolderPo);
        Long fileId = userFileService.createFolder(folderContext);
        return R.data(fileId.toString());
    }

    /**
     * 文件重命名
     */
    @PutMapping("file/rename")
    public R updateFilename(@Validated @RequestBody UpdateFilenamePo updateFilenamePo) {
        UpdateFilenameContext context = fileConverter.UpdateFilenamePo2UpdateFilenameContext(updateFilenamePo);
        userFileService.updateFilename(context);
        return R.success();
    }


    /**
     * 批量删除文件
     */
    @DeleteMapping("file/delete")
    public R deleteFile(@Validated @RequestBody DeleteFilePo deleteFilePo) {
        DeleteFileContext context = fileConverter.DeleteFilePo2DeleteFileContext(deleteFilePo);
        String fileIds = deleteFilePo.getFileIds();
        List<Long> fileIdList = Splitter.on(RPanConstants.COMMON_SEPARATOR).splitToList(fileIds).stream().map(Long::valueOf).collect(Collectors.toList());
        context.setFileIdList(fileIdList);
        userFileService.deleteFile(context);
        return R.success();
    }

    /**
     * 文件妙传
     */
    @PostMapping("file/sec-upload")
    public R secUpload(@Validated @RequestBody SecUploadFilePo secUploadFilePo) {

        SecUploadFileContext context = fileConverter.SecUploadFilePo2SecUploadFileContext(secUploadFilePo);
        boolean success = userFileService.setUpload(context);
        if (success) {
            return R.success();
        }
        return R.fail("文件唯一标识不存在，请手动执行文件上传的操作");
    }

    /**
     * 文件上传
     *
     * @param fileUploadPo
     * @return
     */
    @PostMapping("file/upload")
    public R upload(@Validated FileUploadPo fileUploadPo) {
        FileUploadContext context = fileConverter.FileUploadPo2FileUploadContext(fileUploadPo);
        userFileService.upload(context);
        return R.success();
    }

    /**
     * 文件分片上传
     */
    @PostMapping("file/chunk-upload")
    public R chunkUpload(@Validated FileChunkUploadPo fileChunkUploadPo) {
        FileChunkUploadContext context = fileConverter.FileChunkUploadPo2FileChunkUploadContext(fileChunkUploadPo);
        FileChunkUploadVO vo = userFileService.chunkUpload(context);
        return R.data(vo);
    }

    /**
     * 文件分片上传-分片检查
     *
     * @param queryUploadedChunksPo
     * @return
     */
    @GetMapping("file/chunk-upload")
    public R<uploadedChunksVo> getUploadedChunks(@Validated QueryUploadedChunksPo queryUploadedChunksPo) {
        UploadedChunksContext context = fileConverter.QueryUploadedChunksPo2uploadedChunksContext(queryUploadedChunksPo);
        uploadedChunksVo vo = userFileService.getUploadedChunks(context);
        return R.data(vo);
    }

    /**
     * 文件分片上传-文件合并
     */
    @GetMapping("file/merge")
    public R mergeFile(@Validated @RequestBody FileChunkMergePo fileChunkMergePo) {
        FileChunkMergeContext context = fileConverter.FileChunkMergePo2FileChunkMergeContext(fileChunkMergePo);
        userFileService.mergeFile(context);
        return R.success();
    }
}
