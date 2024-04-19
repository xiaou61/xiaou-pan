package com.xiaou.storage.engine.oss;

import com.xiaou.pan.storage.engine.core.AbstractStorageEngine;
import com.xiaou.pan.storage.engine.core.context.DeleteFileContext;
import com.xiaou.pan.storage.engine.core.context.StoreFileContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 基于oss
 */
@Component
public class OSSStorageEngine extends AbstractStorageEngine {
    @Override
    protected void doDelete(DeleteFileContext context) throws IOException {

    }

    @Override
    protected void doStore(StoreFileContext context) throws IOException {

    }
}
