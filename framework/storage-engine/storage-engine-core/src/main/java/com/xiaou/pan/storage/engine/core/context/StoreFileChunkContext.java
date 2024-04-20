package com.xiaou.pan.storage.engine.core.context;

import lombok.Data;
import org.mockito.internal.matchers.Or;

import java.io.InputStream;
import java.io.Serializable;

@Data
public class StoreFileChunkContext implements Serializable {

    private static final long serialVersionUID = 873110129082629647L;

    private String filename;

    private String identifier;

    private Long totalSize;

    private InputStream inputStream;

    private String realPath;

    private Integer totalChunks;

    private Integer chunkNumber;

    private Long currentChunkSize;

    private Long userId;
}
