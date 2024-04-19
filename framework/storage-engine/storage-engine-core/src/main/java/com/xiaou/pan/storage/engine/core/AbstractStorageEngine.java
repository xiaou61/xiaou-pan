package com.xiaou.pan.storage.engine.core;

import cn.hutool.core.lang.Assert;
import com.xiaou.pan.core.constants.CacheConstants;
import com.xiaou.pan.core.exception.RPanBusinessException;
import com.xiaou.pan.storage.engine.core.context.DeleteFileContext;
import com.xiaou.pan.storage.engine.core.context.StoreFileContext;
import org.springframework.beans.factory.annotation.Autowired;
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
}
