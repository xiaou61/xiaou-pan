package com.xiaou.pan.server.modules.file.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaou.pan.core.exception.RPanBusinessException;
import com.xiaou.pan.core.utils.IdUtil;
import com.xiaou.pan.server.common.config.PanServerConfig;
import com.xiaou.pan.server.modules.file.context.FileChunkSaveContext;
import com.xiaou.pan.server.modules.file.converter.FileConverter;
import com.xiaou.pan.server.modules.file.domain.UPanFile;
import com.xiaou.pan.server.modules.file.domain.UPanFileChunk;
import com.xiaou.pan.server.modules.file.enums.MergeFlagEnum;
import com.xiaou.pan.server.modules.file.service.IFileChunkService;
import com.xiaou.pan.server.modules.file.mapper.UPanFileChunkMapper;
import com.xiaou.pan.storage.engine.core.StorageEngine;
import com.xiaou.pan.storage.engine.core.context.StoreFileChunkContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author Lenovo
 * @description 针对表【u_pan_file_chunk(文件分片信息表)】的数据库操作Service实现
 * @createDate 2024-04-13 15:26:52
 */
@Service
public class FileChunkServiceImpl extends ServiceImpl<UPanFileChunkMapper, UPanFileChunk>
        implements IFileChunkService {


    @Autowired
    private PanServerConfig config;

    @Autowired
    private FileConverter fileConverter;

    @Autowired
    private StorageEngine storageEngine;


    /**
     * 文件分片保存
     * 1.保存文件分片和记录
     * 2.判断文件分片是否全部上传完成
     *
     * @param context
     */
    @Override
    public synchronized void saveChunkFile(FileChunkSaveContext context) {
        doSaveChunkFile(context);
        doJudgeMergeFile(context);
    }

    /**
     * 执行文件上次分片保存的操作
     * 1.委托文件存储引擎去存储文件分片
     * 2.保存文件分片记录
     *
     * @param context
     */
    private void doJudgeMergeFile(FileChunkSaveContext context) {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("identifier", context.getIdentifier());
        queryWrapper.eq("create_user", context.getUserId());
        int count = count(queryWrapper);
        if (count == context.getTotalChunks().intValue()) {
            //所有的分片上传完成
            context.setMergeFlagEnum(MergeFlagEnum.READY);
        }
    }

    /**
     * 保存文件分片记录
     *
     * @param context
     */
    private void doSaveRecord(FileChunkSaveContext context) {
        UPanFileChunk record = new UPanFileChunk();
        record.setId(IdUtil.get());
        record.setIdentifier(context.getIdentifier());
        record.setRealPath(context.getRealPath());
        record.setChunkNumber(context.getChunkNumber());
        record.setExpirationTime(DateUtil.offsetDay(new Date(), config.getChunkFileExpirationDays()));
        record.setCreateUser(IdUtil.get());
        record.setCreateTime(new Date());
        if (!save(record)) {
            throw new RPanBusinessException("文件分片上传失败");
        }
    }

    /**
     * 委托文件存储引擎去存储文件分片
     *
     * @param context
     */
    private void doStoreFileChunk(FileChunkSaveContext context) {
        try {
            StoreFileChunkContext storeFileChunkContext = fileConverter.fileChunkSaveContext2StoreFileChunkContext(context);
            storeFileChunkContext.setInputStream(context.getFile().getInputStream());
            storageEngine.storeChunk(storeFileChunkContext);
            context.setRealPath(storeFileChunkContext.getRealPath());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RPanBusinessException("文件分片上传失败");
        }

    }

    /**
     * 判断是否所有的分片均上传完成
     *
     * @param context
     */
    private void doSaveChunkFile(FileChunkSaveContext context) {
        doStoreFileChunk(context);
        doSaveRecord(context);
    }
}




