package com.bigcrab.spring.cache;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.stream.Collectors;


/**
 * Created by luantao on 2017/3/6.
 */
@Configuration
@EnableCaching(proxyTargetClass = true)
public class AppConfig implements CachingConfigurer {

    private CacheManager cacheManager;

    @PostConstruct
    public void init() {
        cacheManager = new ConcurrentMapCacheManager();
    }

    @Override
    public CacheManager cacheManager() {
        return null;
    }

    @Override
    public CacheResolver cacheResolver() {
        return context -> context.getOperation().getCacheNames()
                .stream()
                .map(cacheManager::getCache)
                .collect(Collectors.toList());
    }

    @Override
    public KeyGenerator keyGenerator() {
        return null;
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return null;
    }

}
