package com.xiaou.pan.storage.engine.core;

import cn.hutool.core.lang.Assert;
import com.xiaou.pan.core.constants.CacheConstants;
import com.xiaou.pan.core.exception.RPanBusinessException;
import com.xiaou.pan.storage.engine.core.context.*;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Objects;

/**
 * 顶级文件存储引擎的共用父类
 */
public abstract class AbstractStorageEngine implements StorageEngine {

    @Resource
    private CacheManager cacheManager;

    /**
     * 公用的获取缓存的方法
     *
     * @return
     */
    protected Cache getCache() {
        if (Objects.isNull(cacheManager)) {
            throw new RPanBusinessException("the cache manager is empty!");
        }
        return cacheManager.getCache(CacheConstants.U_PAN_CACHE_NAME);
    }

    /**
     * 存储物理引擎
     * 1.参数校验
     * 2.执行动作
     *
     * @param context
     * @throws IOException
     */
    @Override
    public void store(StoreFileContext context) throws IOException {
        checkStoreFileContext(context);
        doStore(context);
    }


    @Override
    public void delete(DeleteFileContext context) throws IOException {
        checkDeleteFileContext(context);
        doDelete(context);
    }


    protected abstract void doDelete(DeleteFileContext context) throws IOException;


    /**
     * 校验删除物理文件的上下文信息
     *
     * @param context
     */
    private void checkDeleteFileContext(DeleteFileContext context) {
        Assert.notEmpty(context.getRealFilePathList(), "文件路径不能为空");
    }


    /**
     * 存储文件到物理引擎 交给下面的子类去实现
     *
     * @param context
     */
    protected abstract void doStore(StoreFileContext context) throws IOException;

    /**
     * 校验上下文信息
     *
     * @param context
     */
    private void checkStoreFileContext(StoreFileContext context) {
        Assert.notBlank(context.getFilename(), "文件名称不能为空");
        Assert.notNull(context.getTotalSize(), "文件总大小不能为空");
        Assert.notNull(context.getInputStream(), "文件不能为空");
    }

    /**
     * 存储物理文件的分片
     * 1.参数校验
     * 2.执行动作
     *
     * @param context
     * @throws IOException
     */
    @Override
    public void storeChunk(StoreFileChunkContext context) throws IOException {
        checkStoreFileChunkContext(context);
        doStoreChunk(context);
    }

    /**
     * 执行保存文件分片，下城到底层去实现
     *
     * @param context
     * @throws IOException
     */
    protected abstract void doStoreChunk(StoreFileChunkContext context) throws IOException;

    /**
     * 校验保存文件分片的参数
     *
     * @param context
     */
    private void checkStoreFileChunkContext(StoreFileChunkContext context) {
        Assert.notBlank(context.getFilename(), "文件名称不能为空");
        Assert.notBlank(context.getIdentifier(), "文件唯一标识不能为空");
        Assert.notNull(context.getTotalSize(), "文件总大小不能为空");
        Assert.notNull(context.getInputStream(), "文件不能为空");
        Assert.notNull(context.getChunkNumber(), "分片序号不能为空");
        Assert.notNull(context.getTotalChunks(), "文件分片总数不能为空");
        Assert.notNull(context.getUserId(), "用户ID不能为空");
        Assert.notNull(context.getCurrentChunkSize(), "当前分片大小不能为空");
    }

    /**
     * 合并文件分片
     * 1.检查参数
     * 2.执行动作
     *
     * @param context
     * @throws IOException
     */
    @Override
    public void mergeFile(MergeFileContext context) throws IOException {
        checkMergeFileContext(context);
        doMergeFile(context);
    }

    /**
     * 执行文件合并的动作，下沉到子类实现
     * @param context
     */
    protected abstract void doMergeFile(MergeFileContext context) throws IOException;

    /**
     * 检查文件合并的上下文实体信息
     *
     * @param context
     */
    private void checkMergeFileContext(MergeFileContext context) {
        Assert.notBlank(context.getFilename(), "文件名称不能为空");
        Assert.notBlank(context.getIdentifier(), "文件唯一标识不能为空");
        Assert.notNull(context.getUserId(), "用户ID不能为空");
        Assert.notEmpty(context.getRealPathList(), "文件总大小不能为空");
    }

    /**
     * 1.参数校验
     * 2.执行动作
     * @param context
     * @throws IOException
     */
    @Override
    public void realFile(ReadFileContext context) throws IOException {
        checkRealFileContext(context);
        doRealFile(context);
    }

    /**
     * 读取文件内容并写入输出流中
     * @param context
     */
    protected abstract void doRealFile(ReadFileContext context) throws IOException;

    /**
     * 文件读取参数校验
     * @param context
     */
    private void checkRealFileContext(ReadFileContext context) {
        Assert.notBlank(context.getRealPath(),"文件路径不能为空");
        Assert.notNull(context.getOutputStream(),"输出流不能为空");
    }
}
