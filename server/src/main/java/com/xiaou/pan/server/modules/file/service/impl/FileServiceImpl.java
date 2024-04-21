package com.xiaou.pan.server.modules.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.xiaou.pan.core.exception.RPanBusinessException;
import com.xiaou.pan.core.utils.FileUtils;
import com.xiaou.pan.core.utils.IdUtil;
import com.xiaou.pan.server.common.event.log.ErrorLogEvent;
import com.xiaou.pan.server.modules.file.context.FileChunkMergeAndSaveContext;
import com.xiaou.pan.server.modules.file.context.FileSaveContext;
import com.xiaou.pan.server.modules.file.domain.UPanFile;
import com.xiaou.pan.server.modules.file.domain.UPanFileChunk;
import com.xiaou.pan.server.modules.file.mapper.UPanFileMapper;
import com.xiaou.pan.server.modules.file.service.IFileChunkService;
import com.xiaou.pan.server.modules.file.service.IFileService;
import com.xiaou.pan.storage.engine.core.StorageEngine;
import com.xiaou.pan.storage.engine.core.context.DeleteFileContext;
import com.xiaou.pan.storage.engine.core.context.MergeFileContext;
import com.xiaou.pan.storage.engine.core.context.StoreFileContext;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Lenovo
 * @description 针对表【u_pan_file(物理文件信息表)】的数据库操作Service实现
 * @createDate 2024-04-13 15:26:52
 */
@Service
public class FileServiceImpl extends ServiceImpl<UPanFileMapper, UPanFile>
        implements IFileService, ApplicationContextAware {

    @Autowired
    private StorageEngine storageEngine;

    @Setter
    private ApplicationContext applicationContext;

    @Autowired
    private IFileChunkService fileChunkService;

    /**
     * 上传单文件保存实体记录
     * 1.上传单文件
     * 2.保存实体记录
     *
     * @param context
     */
    @Override
    public void saveFile(FileSaveContext context) {
        storeMultipartFile(context);
        UPanFile record = doSaveFile(context.getFilename(),
                context.getRealPath(),
                context.getTotalSize(),
                context.getIdentifier(),
                context.getUserId());
        context.setRecord(record);
    }

    /**
     * 合并物理文件并保存物理文件记录
     * 1.委托文件引擎合并文件分片
     * 2.保存物理文件记录
     *
     * @param context
     */
    @Override
    public void mergeFileChunkAndSave(FileChunkMergeAndSaveContext context) {
        doMergeFileChunk(context);
        UPanFile record = doSaveFile(context.getFilename(),
                context.getRealPath(),
                context.getTotalSize(),
                context.getIdentifier(),
                context.getUserId());
        context.setRecord(record);
    }


    /*****************************************************private*****************************************************/


    /**
     * 委托文件存储引擎合并文件分片
     * 1.查询文件分片的记录
     * 2.根据文件分片的记录去合并物理文件
     * 3.删除文件分片的记录
     * 4.封装合并文件的真实存储路径到上下文
     *
     * @param context
     */
    private void doMergeFileChunk(FileChunkMergeAndSaveContext context) {
        QueryWrapper<UPanFileChunk> queryWrapper = Wrappers.query();
        queryWrapper.eq("identifier", context.getIdentifier());
        queryWrapper.eq("create_user", context.getUserId());
        queryWrapper.ge("expiration_time", new Date());
        List<UPanFileChunk> chunkRecoredList = fileChunkService.list(queryWrapper);
        if (CollectionUtils.isEmpty(chunkRecoredList)) {
            throw new RPanBusinessException("该文件未找到分片记录");
        }
        List<String> realPathList = chunkRecoredList.stream().
                sorted(Comparator.comparing(UPanFileChunk::getChunkNumber))
                .map(UPanFileChunk::getRealPath).collect(Collectors.toList());

        //TODO 委托存储引擎去合并文件分片
        try {
            MergeFileContext mergeFileContext=new MergeFileContext();
            mergeFileContext.setFilename(context.getFilename());
            mergeFileContext.setIdentifier(context.getIdentifier());
            mergeFileContext.setUserId(context.getUserId());
            mergeFileContext.setRealPathList(realPathList);
            storageEngine.mergeFile(mergeFileContext);
            context.setRealPath(mergeFileContext.getRealPath());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RPanBusinessException("文件合并失败");
        }

        List<Long> fileChunkRecordIdList = chunkRecoredList.stream().map(UPanFileChunk::getId).collect(Collectors.toList());
        fileChunkService.removeByIds(fileChunkRecordIdList);

        //TODO 封装实体文件的真实存储路径

    }


    private UPanFile doSaveFile(String filename, String realPath, Long totalSize, String identifier, Long userId) {
        UPanFile record = assembleUPanFile(filename, realPath, totalSize, identifier, userId);
        if (!save(record)) {
            try {
                DeleteFileContext deleteFileContext = new DeleteFileContext();
                deleteFileContext.setRealFilePathList(Lists.newArrayList(realPath));
                storageEngine.delete(deleteFileContext);
            } catch (IOException e) {
                e.printStackTrace();
                //广播一个事件
                ErrorLogEvent errorLogEvent = new ErrorLogEvent(this, "文件物理删除失败，清执行手动删除！文件路径" + realPath, userId);
                applicationContext.publishEvent(errorLogEvent);
            }
        }
        return record;
    }

    private UPanFile assembleUPanFile(String filename, String realPath, Long totalSize, String identifier, Long userId) {
        UPanFile record = new UPanFile();

        record.setFileId(IdUtil.get());
        record.setFilename(filename);
        record.setRealPath(realPath);
        record.setFileSize(String.valueOf(totalSize));
        record.setFileSizeDesc(FileUtils.byteCountToDisplaySize(totalSize));
        record.setFileSuffix(FileUtils.getFileSuffix(filename));
        record.setIdentifier(identifier);
        record.setCreateUser(userId);
        record.setCreateTime(new Date());
        return record;
    }


    /**
     * 上传单文件
     * 该方法委托文件上传引擎实现。
     *
     * @param context
     */
    private void storeMultipartFile(FileSaveContext context) {

        try {
            StoreFileContext storeFileContext = new StoreFileContext();
            storeFileContext.setFilename(context.getFilename());
            storeFileContext.setTotalSize(context.getTotalSize());
            storeFileContext.setInputStream(context.getFile().getInputStream());
            storeFileContext.setRealPath(context.getRealPath());
            storageEngine.store(storeFileContext);
            context.setRealPath(storeFileContext.getRealPath());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RPanBusinessException("文件上传失败");
        }
    }


}




