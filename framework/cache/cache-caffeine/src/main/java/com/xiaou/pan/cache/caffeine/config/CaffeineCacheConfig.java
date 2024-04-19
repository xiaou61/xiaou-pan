package com.xiaou.pan.cache.caffeine.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.xiaou.pan.core.constants.CacheConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;

import javax.annotation.Resource;

/**
 * 自定义配置类
 */
@SpringBootConfiguration
@EnableCaching
@Slf4j
public class CaffeineCacheConfig {

    @Resource
    private CaffeineCacheProperties properties;

    @Bean
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(CacheConstants.U_PAN_CACHE_NAME);
        cacheManager.setAllowNullValues(properties.isAllowNullValue());
        Caffeine<Object, Object> caffeineBuilder = Caffeine.newBuilder()
                .initialCapacity(properties.getInitCacheCapacity())
                .maximumSize(properties.getMaxCacheCapacity());
        cacheManager.setCaffeine(caffeineBuilder);
        log.info("the caffeine cache manager is loaded successfully!");
        return cacheManager;
    }
}
