package com.xiaou.pan.server.common.config;


import com.xiaou.pan.core.constants.RPanConstants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("com.xiaou.pan.server")
@Data
public class PanServerConfig {

    /**
     * 过期天数
     */
    public Integer chunkFileExpirationDays= RPanConstants.ONE_INT;
}
