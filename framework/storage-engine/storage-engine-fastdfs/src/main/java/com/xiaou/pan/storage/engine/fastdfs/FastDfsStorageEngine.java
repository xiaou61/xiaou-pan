package com.xiaou.pan.storage.engine.fastdfs;

import com.xiaou.pan.storage.engine.core.AbstractStorageEngine;
import com.xiaou.pan.storage.engine.core.context.DeleteFileContext;
import com.xiaou.pan.storage.engine.core.context.StoreFileContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 基于fastdfs实现的存储引擎
 */
@Component
public class FastDfsStorageEngine extends AbstractStorageEngine {
    @Override
    protected void doDelete(DeleteFileContext context) throws IOException {

    }

    @Override
    protected void doStore(StoreFileContext context) throws IOException {

    }
}
