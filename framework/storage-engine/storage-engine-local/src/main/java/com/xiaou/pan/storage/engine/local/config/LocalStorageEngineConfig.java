package com.xiaou.pan.storage.engine.local.config;

import com.xiaou.pan.core.utils.FileUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "com.xiaou.pan.storage.engine.local")
@Data
public class LocalStorageEngineConfig {
    private String rootFilePath = FileUtils.generateDefaultStoreFileRealPath();


    /**
     * 实际存储文件分片的路径的前缀
     */
    private String rootFileChunkPath = FileUtils.generateDefaultStoreFileChunkRealPath();

}
