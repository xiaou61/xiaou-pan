package com.xiaou.pan.cache.caffeine.config;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "com.xiaou.pan.cache.caffeine")
public class CaffeineCacheProperties {
    /**
     * 缓存初始容量
     */
    private Integer initCacheCapacity = 256;
    /**
     * 缓存最大容量 超过之后会按照recently or very often（最近最少）策略进行缓存剔除
     */
    private Long maxCacheCapacity = 10000L;


    /**
     * 是否允许空值null作为缓存的value
     */
    private boolean allowNullValue = Boolean.TRUE;

}
