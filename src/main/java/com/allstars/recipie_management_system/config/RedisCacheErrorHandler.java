package com.allstars.recipie_management_system.config;

import io.lettuce.core.RedisCommandTimeoutException;
import net.bytebuddy.implementation.bytecode.Throw;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;

public class RedisCacheErrorHandler implements CacheErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(RedisCacheErrorHandler.class);


    @Override
    public void handleCacheGetError(RuntimeException e, Cache cache, Object o) {
        handleTimeOutException(e);
        log.info("Unable to get from cache " + cache.getName() + " : " + e.getMessage());
    }

    @Override
    public void handleCachePutError(RuntimeException e, Cache cache, Object o, Object o1) {
        handleTimeOutException(e);
        log.info("Unable to put into cache " + cache.getName() + " : " + e.getMessage());
    }

    @Override
    public void handleCacheEvictError(RuntimeException e, Cache cache, Object o) {
        handleTimeOutException(e);
        log.info("Unable to evict from cache " + cache.getName() + " : " + e.getMessage());
    }

    @Override
    public void handleCacheClearError(RuntimeException e, Cache cache) {
        handleTimeOutException(e);
        log.info("Unable to clean cache " + cache.getName() + " : " + e.getMessage());
    }

    private void handleTimeOutException(RuntimeException exception) {

        if (exception instanceof RedisCommandTimeoutException)
            return;
    }
}
