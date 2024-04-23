package com.xiaou.pan.server.modules.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.xiaou.pan.core.constants.RPanConstants;
import com.xiaou.pan.core.exception.RPanBusinessException;
import com.xiaou.pan.core.utils.FileUtils;
import com.xiaou.pan.core.utils.IdUtil;
import com.xiaou.pan.server.common.event.file.DeleteFileEvent;
import com.xiaou.pan.server.common.utils.HttpUtil;
import com.xiaou.pan.server.modules.file.constants.FileConstants;
import com.xiaou.pan.server.modules.file.context.*;
import com.xiaou.pan.server.modules.file.context.FileUploadContext;
import com.xiaou.pan.server.modules.file.converter.FileConverter;
import com.xiaou.pan.server.modules.file.domain.UPanFile;
import com.xiaou.pan.server.modules.file.domain.UPanUserFile;
import com.xiaou.pan.server.modules.file.enums.DelFlagEnum;
import com.xiaou.pan.server.modules.file.enums.FileTypeEnum;
import com.xiaou.pan.server.modules.file.enums.FolderFlagEnum;
import com.xiaou.pan.server.modules.file.service.IFileChunkService;
import com.xiaou.pan.server.modules.file.service.IFileService;
import com.xiaou.pan.server.modules.file.service.IUserFileService;
import com.xiaou.pan.server.modules.file.mapper.UPanUserFileMapper;
import com.xiaou.pan.server.modules.file.vo.FileChunkUploadVO;
import com.xiaou.pan.server.modules.file.vo.FolderTreeNodeVO;
import com.xiaou.pan.server.modules.file.vo.UPanUserFileVO;
import com.xiaou.pan.server.modules.file.vo.uploadedChunksVo;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Lenovo
 * @description 针对表【u_pan_user_file(用户文件信息表)】的数据库操作Service实现
 * @createDate 2024-04-13 15:26:52
 */
@Service("userFileService")
public class UserFileServiceImpl extends ServiceImpl<UPanUserFileMapper, UPanUserFile>
        implements IUserFileService, ApplicationContextAware {


    @Setter
    private ApplicationContext applicationContext;

    @Autowired
    private IFileService fileService;

    @Autowired
    private FileConverter fileConverter;

    @Autowired
    private IFileChunkService fileChunkService;


    /**
     * 创建文件夹信息
     *
     * @param createFolderContext
     * @return
     */
    @Override
    public Long createFolder(CreateFolderContext createFolderContext) {
        return saveUserFile(createFolderContext.getParentId(),
                createFolderContext.getFolderName(),
                FolderFlagEnum.YES,
                null,
                null,
                createFolderContext.getUserId(),
                null);
    }

    /**
     * 查询用户的根文件夹信息
     *
     * @param userId
     * @return
     */
    @Override
    public UPanUserFile getUserRootFile(Long userId) {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("parent_id", FileConstants.TOP_PARENT_ID);
        queryWrapper.eq("del_flag", DelFlagEnum.NOT_DELETE.getCode());
        queryWrapper.eq("folder_flag", FolderFlagEnum.YES.getCode());
        return getOne(queryWrapper);
    }

    /**
     * 查询用户的文件列表
     *
     * @param context
     * @return
     */
    @Override
    public List<UPanUserFileVO> getFileList(QueryFileListContext context) {
        return baseMapper.selectFileList(context);
    }

    /**
     * 更新文件名称
     * 1.检测更新文件名称的条件
     * 2.执行操作
     *
     * @param context
     */
    @Override
    public void updateFilename(UpdateFilenameContext context) {
        checkUpdateFilenameCondition(context);
        doUpdateFilename(context);
    }

    /**
     * 批量删除用户文件
     * 1.校验删除的条件是否符合
     * 2.执行批量删除的动作
     * 3.发布批量删除文件的事件，给其他模块订阅使用
     *
     * @param context
     */
    @Override
    public void deleteFile(DeleteFileContext context) {
        checkFileDeleteCondition(context);
        doDeleteFile(context);
        afterFileDelete(context);
    }

    /**
     * 文件妙传
     * 1.通过文件的唯一标识，查找对应的实体文件记录
     * 2.如果没有查到，直接返回妙传失败
     * 3.如果查找记录，直接挂在关联关系，返回妙传成功
     *
     * @param context
     * @return
     */
    @Override
    public boolean setUpload(SecUploadFileContext context) {
        UPanFile record = getFileByUserIdAndIdentifier(context.getUserId(), context.getIdentifier());
        if (Objects.isNull(record)) {
            return false;
        }
        saveUserFile(context.getParentId(),
                context.getFilename(),
                FolderFlagEnum.NO,
                FileTypeEnum.getFileTypeCode(FileUtils.getFileSuffix(context.getFilename())),
                record.getFileId(),
                context.getUserId(),
                record.getFileSizeDesc());
        return true;
    }

    /**
     * 文件上传
     * 1.上传文件并保存实体文件的记录
     * 2.保存用户文件的关系记录
     *
     * @param context
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void upload(FileUploadContext context) {
        saveFile(context);
        saveUserFile(context.getParentId(),
                context.getFilename(),
                FolderFlagEnum.NO,
                FileTypeEnum.getFileTypeCode(FileUtils.getFileSuffix(context.getFilename())),
                context.getRecord().getFileId(),
                context.getUserId(),
                context.getRecord().getFileSizeDesc());
    }

    /**
     * 文件分片上传
     * 1.上传实体文件
     * 2.保存分片文件的记录
     * 3.校验是否全部分片上传完成
     *
     * @param context
     * @return
     */
    @Override
    public FileChunkUploadVO chunkUpload(FileChunkUploadContext context) {
        FileChunkSaveContext fileChunkSaveContext = fileConverter.FileChunkUploadContext2FileChunkSaveContext(context);
        fileChunkService.saveChunkFile(fileChunkSaveContext);
        FileChunkUploadVO vo = new FileChunkUploadVO();
        vo.setMergeFlag(fileChunkSaveContext.getMergeFlagEnum().getCode());
        return vo;
    }

    /**
     * 1.查询已上传的分片列表
     * 2.封装返回实体
     *
     * @param context
     * @return
     */
    @Override
    public uploadedChunksVo getUploadedChunks(UploadedChunksContext context) {
        QueryWrapper queryWrapper = Wrappers.query();
        queryWrapper.select("chunk_number");
        queryWrapper.eq("identifier", context.getIdentifier());
        queryWrapper.eq("create_user", context.getUserId());
        queryWrapper.gt("expiration_time", new Date());
        List<Integer> uploadedChunks = fileChunkService.listObjs(queryWrapper, value -> (Integer) value);

        uploadedChunksVo vo = new uploadedChunksVo();
        vo.setUploadedChunks(uploadedChunks);
        return vo;
    }

    /**
     * 文件分片合并
     * <p>
     * 1.文件分片物理合并
     * 2.保存文件实体记录
     * 3.保存文件用户关系映射
     *
     * @param context
     */
    @Override
    public void mergeFile(FileChunkMergeContext context) {
        mergeFileChunkAndSave(context);

        saveUserFile(context.getParentId(),
                context.getFilename(),
                FolderFlagEnum.NO,
                FileTypeEnum.getFileTypeCode(FileUtils.getFileSuffix(context.getFilename())),
                context.getRecord().getFileId(),
                context.getUserId(),
                context.getRecord().getFileSizeDesc());

    }

    /**
     * 文件下载
     * 1.参数校验：文件是否存在，文件是否属于该用户
     * 2.检验该文件是不是一个文件夹
     * 3.执行下载
     *
     * @param context
     */
    @Override
    public void download(FileDownloadContext context) {
        UPanUserFile record = getById(context.getFileId());
        checkOperatePermission(record, context.getUserId());
        if (checkIsFolder(record)) {
            throw new RPanBusinessException("文件夹不能下载");
        }
        doDownload(record, context.getResponse());
    }

    /**
     * 文件预览
     * 1.参数校验
     * 2.校验是否是文件夹
     * 3.执行预览操作
     *
     * @param context
     */
    @Override
    public void preview(FilePreviewContext context) {
        UPanUserFile record = getById(context.getFileId());
        checkOperatePermission(record, context.getUserId());
        if (checkIsFolder(record)) {
            throw new RPanBusinessException("文件夹不能下载");
        }
        doPreview(record, context.getResponse());
    }

    /**
     * 查询用户的文件夹树
     * 1.查询出该用户的所有文件夹列表
     * 2.在内存中拼接文件夹树
     *
     * @param context
     * @return
     */
    @Override
    public List<FolderTreeNodeVO> getFolderTree(QueryFolderTreeContext context) {
        List<UPanUserFile> folderRecords = queryFolderRecords(context.getUserId());
        List<FolderTreeNodeVO> result = assembleFolderTreeNodeVOList(folderRecords);
        return result;
    }



    /*****************************************************private*****************************************************/


    /**
     * 拼装文件夹树列表
     *
     * @param folderRecords
     * @return
     */
    private List<FolderTreeNodeVO> assembleFolderTreeNodeVOList(List<UPanUserFile> folderRecords) {
        if (CollectionUtils.isEmpty(folderRecords)) {
            return Lists.newArrayList();
        }

        List<FolderTreeNodeVO> mappedFolderTreeNodeVOList = folderRecords.stream().map(fileConverter::uPanUserFile2FolderTreeNodeVo).collect(Collectors.toList());
        Map<Long, List<FolderTreeNodeVO>> mappedFolderTreeNodeVOMap = mappedFolderTreeNodeVOList.stream().collect(Collectors.groupingBy(FolderTreeNodeVO::getParentId));
        for (FolderTreeNodeVO node : mappedFolderTreeNodeVOList) {
            List<FolderTreeNodeVO> children = mappedFolderTreeNodeVOMap.get(node.getId());
            if (CollectionUtils.isNotEmpty(children)) {
                node.getChildren().addAll(children);
            }
        }
        return mappedFolderTreeNodeVOList.stream().filter(node -> Objects.equals(node.getParentId(), FileConstants.TOP_PARENT_ID)).collect(Collectors.toList());
    }

    /**
     * 查询用户所有有效文件夹列表
     *
     * @param userId
     * @return
     */
    private List<UPanUserFile> queryFolderRecords(Long userId) {
        QueryWrapper queryWrapper = Wrappers.query();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("folder_flag", FolderFlagEnum.YES.getCode());
        queryWrapper.eq("del_flag", DelFlagEnum.NOT_DELETE.getCode());
        return list(queryWrapper);
    }


    /**
     * 预览
     *
     * @param record
     * @param response
     */
    private void doPreview(UPanUserFile record, HttpServletResponse response) {
        UPanFile realFileRecord = fileService.getById(record.getRealFileId());
        if (Objects.isNull(realFileRecord)) {
            throw new RPanBusinessException("文件不存在");
        }
        addCommonResponseHeader(response, realFileRecord.getFilePreviewContentType());
        realFile2OutputStream(realFileRecord.getRealPath(), response);
    }


    /**
     * 执行文件下载动作
     * <p>
     * 1.查询文件的真实存储路径
     * 2.添加跨域的公共响应头
     * 3.拼装下载文件的名称、长度等等响应信息
     * 4.委托文件存储引擎去读取文件内容到响应的输出流种
     *
     * @param record
     * @param response
     */
    private void doDownload(UPanUserFile record, HttpServletResponse response) {
        UPanFile realFileRecord = fileService.getById(record.getRealFileId());
        if (Objects.isNull(realFileRecord)) {
            throw new RPanBusinessException("文件不存在");
        }
        addCommonResponseHeader(response, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        addDownloadAttribute(response, record, realFileRecord);
        realFile2OutputStream(realFileRecord.getRealPath(), response);
    }

    /**
     * 委托文件存储引擎去读取文件内容，并且写入到输出流中
     *
     * @param realPath
     * @param response
     */
    private void realFile2OutputStream(String realPath, HttpServletResponse response) {

    }

    /**
     * 添加文件下载的响应信息
     *
     * @param response
     * @param record
     * @param realFileRecord
     */
    private void addDownloadAttribute(HttpServletResponse response, UPanUserFile record, UPanFile realFileRecord) {
        try {
            response.addHeader(FileConstants.CONTENT_DISPOSITION_STR,
                    FileConstants.CONTENT_DISPOSITION_VALUE_PREFIX_STR + new String(record.getFileName().getBytes(FileConstants.GB2312_STR), FileConstants.IOS_8859_1_STR));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RPanBusinessException("文件下载失败");
        }
        response.setContentLengthLong(Long.parseLong(realFileRecord.getFileSize()));
    }

    /**
     * 添加公共的响应头
     *
     * @param response
     */
    private void addCommonResponseHeader(HttpServletResponse response, String contentTypeValue) {
        response.reset();
        HttpUtil.addCorsResponseHeaders(response);
        response.addHeader(FileConstants.CONTENT_TYPE_STR, contentTypeValue);
        response.setContentType(contentTypeValue);
    }

    /**
     * 检查当前记录是不是一个文件夹
     *
     * @param record
     * @return
     */
    private boolean checkIsFolder(UPanUserFile record) {
        if (Objects.isNull(record)) {
            throw new RPanBusinessException("文件不存在");
        }

        return FolderFlagEnum.YES.getCode().equals(record.getFolderFlag());
    }

    /**
     * 校验用户的操作权限
     * 1.文件记录必须存在
     * 2.文件记录的创建者必须是该登陆用户
     *
     * @param record
     * @param userId
     */
    private void checkOperatePermission(UPanUserFile record, Long userId) {
        if (Objects.isNull(record)) {
            throw new RPanBusinessException("文件不存在");
        }
        if (!record.getUserId().equals(userId)) {
            throw new RPanBusinessException("没有权限操作该文件");


            /**
             * 文件下载
             *
             * @param record
             * @param response
             */
        }
    }


    /**
     * 合并文件分片并保存物理文件记录
     *
     * @param context
     */
    private void mergeFileChunkAndSave(FileChunkMergeContext context) {
        FileChunkMergeAndSaveContext fileChunkMergeAndSaveContext = fileConverter.fileChunkMergeContext2FileChunkMergeAndSaveContext(context);
        fileService.mergeFileChunkAndSave(fileChunkMergeAndSaveContext);
        context.setRecord(fileChunkMergeAndSaveContext.getRecord());
    }

    /**
     * 上传文件并且保存实体文件记录
     * 委托给实体文件的service去完成该操作
     *
     * @param context
     */
    private void saveFile(FileUploadContext context) {
        FileSaveContext fileSaveContext = fileConverter.fileUploadContext2FileSaveContext(context);
        fileService.saveFile(fileSaveContext);
        context.setRecord(fileSaveContext.getRecord());
    }

    /**
     * 根据文件的唯一标识，查找对应的实体文件记录
     *
     * @param userId
     * @param identifier
     * @return
     */
    private UPanFile getFileByUserIdAndIdentifier(Long userId, String identifier) {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("create_user", userId);
        queryWrapper.eq("identifier", identifier);
        List<UPanFile> records = fileService.list(queryWrapper);
        if (CollectionUtils.isEmpty(records)) {
            return null;
        }
        return records.get(RPanConstants.ZERO_INT);
    }


    /**
     * 文件删除的后置操作
     * 1.发布批量删除文件的事件，给其他模块订阅使用
     *
     * @param context
     */
    private void afterFileDelete(DeleteFileContext context) {
        DeleteFileEvent event = new DeleteFileEvent(this, context.getFileIdList());
        applicationContext.publishEvent(event);
    }

    /**
     * 执行文件删除的操作
     *
     * @param context
     */
    private void doDeleteFile(DeleteFileContext context) {
        List<Long> fileIdList = context.getFileIdList();
        UpdateWrapper updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("file_id", fileIdList);
        updateWrapper.set("del_flag", DelFlagEnum.DELETED.getCode());
        updateWrapper.set("update_time", new Date());
        if (!update(updateWrapper)) {
            throw new RPanBusinessException("文件删除失败");
        }
    }

    /**
     * 校验删除的条件是否符合
     * 1.文件ID合法校验
     * 2.用户拥有删除该文件的权限
     *
     * @param context
     */
    private void checkFileDeleteCondition(DeleteFileContext context) {
        List<Long> fileIdList = context.getFileIdList();
        List<UPanUserFile> uPanUserFiles = listByIds(fileIdList);
        if (uPanUserFiles.size() != fileIdList.size()) {
            throw new RPanBusinessException("文件ID不合法");
        }
        Set<Long> fileIdSet = uPanUserFiles.stream().map(UPanUserFile::getFileId).collect(Collectors.toSet());
        int oldSize = fileIdSet.size();
        fileIdSet.addAll(fileIdList);
        int newSize = fileIdSet.size();
        if (newSize != oldSize) {
            throw new RPanBusinessException("文件ID不合法");
        }

        Set<Long> userIdSet = uPanUserFiles.stream().map(UPanUserFile::getUserId).collect(Collectors.toSet());
        if (userIdSet.size() != 1) {
            throw new RPanBusinessException("用户ID不合法");
        }

        Long dbUserId = userIdSet.stream().findFirst().get();

        if (!Objects.equals(dbUserId, context.getUserId())) {
            throw new RPanBusinessException("没有权限删除文件");
        }

    }


    private void doUpdateFilename(UpdateFilenameContext context) {
        UPanUserFile entity = context.getEntity();
        entity.setFileName(context.getNewFilename());
        entity.setUpdateUser(context.getUserId());
        entity.setUpdateTime(new Date());
        if (!updateById(entity)) {
            throw new RPanBusinessException("更新文件名称失败");
        }
    }

    /**
     * 更新文件名称的条件校验
     * 1.文件id是有效的
     * 2.用户有权限更新
     * 3.新旧文件名称不能一样
     * 4.不能使用当前文件夹下面的字文件的名称
     *
     * @param context
     */
    private void checkUpdateFilenameCondition(UpdateFilenameContext context) {
        Long fileId = context.getFileId();
        UPanUserFile entity = getById(fileId);
        if (Objects.isNull(entity)) {
            throw new RPanBusinessException("文件不存在");
        }
        if (!Objects.equals(entity.getUserId(), context.getUserId())) {
            throw new RPanBusinessException("没有权限更新文件");
        }
        if (Objects.equals(entity.getFileName(), context.getNewFilename())) {
            throw new RPanBusinessException("新旧文件名称不能一样");
        }
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", entity.getParentId());
        queryWrapper.eq("filename", context.getNewFilename());
        int count = count(queryWrapper);
        if (count > 0) {
            throw new RPanBusinessException("不能使用当前文件夹下面的字文件的名称");
        }
        context.setEntity(entity);
    }

    /**
     * 保存用户文件的映射记录
     *
     * @param parentId
     * @param fileName
     * @param folderFlagEnum
     * @param fileType
     * @param realFileId
     * @param userId
     * @param fileSizeDesc
     * @return
     */
    private Long saveUserFile(Long parentId,
                              String fileName,
                              FolderFlagEnum folderFlagEnum,
                              Integer fileType,
                              Long realFileId,
                              long userId,
                              String fileSizeDesc) {
        UPanUserFile entity = assembleUPanFUserFile(parentId, fileName, folderFlagEnum, fileType, realFileId, userId, fileSizeDesc);
        if (!save(entity)) {
            throw new RPanBusinessException("创建文件夹失败");
        }
        return entity.getFileId();
    }

    /**
     * 用户文件映射关系实体转换
     * 1.构建并填充实体信息
     * 2.处理文件命名一致的问题
     *
     * @param parentId
     * @param fileName
     * @param folderFlagEnum
     * @param fileType
     * @param realFileId
     * @param userId
     * @param fileSizeDesc
     * @return
     */
    private UPanUserFile assembleUPanFUserFile(Long parentId, String fileName, FolderFlagEnum folderFlagEnum, Integer fileType, Long realFileId, long userId, String fileSizeDesc) {
        UPanUserFile entity = new UPanUserFile();
        entity.setFileId(IdUtil.get());
        entity.setUserId(userId);
        entity.setParentId(parentId);
        entity.setRealFileId(realFileId);
        entity.setFileName(fileName);
        entity.setFolderFlag(folderFlagEnum.getCode());
        entity.setFileSizeDesc(fileSizeDesc);
        entity.setFileType(fileType);
        entity.setDelFlag(DelFlagEnum.NOT_DELETE.getCode());
        entity.setCreateUser(userId);
        entity.setCreateTime(new Date());
        entity.setUpdateUser(userId);
        entity.setUpdateTime(new Date());

        //处理重复文件名称
        handleDuplicateFilename(entity);
        return entity;
    }

    /**
     * 处理重复文件名称
     * 如果同一文件夹下面有文件名称重复
     * 按照系统级规则重命名文件
     *
     * @param entity
     */
    private void handleDuplicateFilename(UPanUserFile entity) {
        String fileName = entity.getFileName(),
                newFilenameWithoutSuffix,
                newFilenameSuffix;
        int newFilenamePointPosition = fileName.lastIndexOf(RPanConstants.POINT_STR);
        if (newFilenamePointPosition == RPanConstants.MINUS_ONE_INT) {
            //说明文件名称没有后缀
            newFilenameWithoutSuffix = fileName;
            newFilenameSuffix = StringUtils.EMPTY;
        } else {
            newFilenameWithoutSuffix = fileName.substring(RPanConstants.ZERO_INT, newFilenamePointPosition);
            newFilenameSuffix = fileName.replace(newFilenameWithoutSuffix, StringUtils.EMPTY);
        }

        int count = getDuplicateFilename(entity, newFilenameWithoutSuffix);
        if (count == 0) {
            return;
        }

        //文件名称重复
        String newFilename = assembleNewFilename(newFilenameWithoutSuffix, count, newFilenameSuffix);
        entity.setFileName(newFilename);
    }

    /**
     * 拼装新文件名称
     *
     * @param newFilenameWithoutSuffix
     * @param count
     * @param newFilenameSuffix
     * @return
     */
    private String assembleNewFilename(String newFilenameWithoutSuffix, int count, String newFilenameSuffix) {
        String newFileName = new StringBuilder(newFilenameWithoutSuffix)
                .append(FileConstants.cn_left_parentheses_str)
                .append(count)
                .append(FileConstants.cn_right_parentheses_str)
                .append(newFilenameSuffix)
                .toString();
        return newFileName;
    }

    /**
     * 查找同一父文件下面的同名文件数量
     *
     * @param entity
     * @param newFilenameWithoutSuffix
     * @return
     */
    private int getDuplicateFilename(UPanUserFile entity, String newFilenameWithoutSuffix) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("parent_id", entity.getParentId());
        queryWrapper.eq("folder_flag", entity.getFolderFlag());
        queryWrapper.eq("user_id", entity.getUserId());
        queryWrapper.eq("del_flag", DelFlagEnum.NOT_DELETE.getCode());
        queryWrapper.likeLeft("filename", newFilenameWithoutSuffix);
        return count(queryWrapper);
    }


}




