package com.xiaou.pan.server.modules.file.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.xiaou.pan.core.exception.RPanBusinessException;
import com.xiaou.pan.core.utils.FileUtils;
import com.xiaou.pan.core.utils.IdUtil;
import com.xiaou.pan.server.common.event.log.ErrorLogEvent;
import com.xiaou.pan.server.modules.file.context.FileSaveContext;
import com.xiaou.pan.server.modules.file.domain.UPanFile;
import com.xiaou.pan.server.modules.file.service.IFileService;
import com.xiaou.pan.server.modules.file.mapper.UPanFileMapper;
import com.xiaou.pan.storage.engine.core.StorageEngine;
import com.xiaou.pan.storage.engine.core.context.DeleteFileContext;
import com.xiaou.pan.storage.engine.core.context.StoreFileContext;
import lombok.Setter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;

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


    /*****************************************************private*****************************************************/


    private UPanFile doSaveFile(String filename, String realPath, Long totalSize, String identifier, Long userId) {
        UPanFile record = assembleUPanFile(filename, realPath, totalSize, identifier, userId);
        if (!save(record)) {
            //TODO 删除已上传的物理文件
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




