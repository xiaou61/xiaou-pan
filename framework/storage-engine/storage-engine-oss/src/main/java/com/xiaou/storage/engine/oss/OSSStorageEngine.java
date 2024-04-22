package com.xiaou.storage.engine.oss;

import com.xiaou.pan.storage.engine.core.AbstractStorageEngine;
import com.xiaou.pan.storage.engine.core.context.*;
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

    @Override
    protected void doStoreChunk(StoreFileChunkContext context) throws IOException {

    }

    @Override
    protected void doMergeFile(MergeFileContext context) {

    }

    @Override
    protected void doRealFile(ReadFileContext context) {

    }
}
