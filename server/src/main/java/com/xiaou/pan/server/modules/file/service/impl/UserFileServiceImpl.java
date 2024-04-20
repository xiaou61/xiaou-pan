package com.xiaou.pan.server.modules.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaou.pan.core.constants.RPanConstants;
import com.xiaou.pan.core.exception.RPanBusinessException;
import com.xiaou.pan.core.utils.FileUtils;
import com.xiaou.pan.core.utils.IdUtil;
import com.xiaou.pan.server.common.event.file.DeleteFileEvent;
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
import com.xiaou.pan.server.modules.file.vo.UPanUserFileVO;
import com.xiaou.pan.server.modules.file.vo.uploadedChunksVo;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

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
     *
     * @param context
     */
    @Override
    public void mergeFile(FileChunkMergeContext context) {

    }


    /*****************************************************private*****************************************************/


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




