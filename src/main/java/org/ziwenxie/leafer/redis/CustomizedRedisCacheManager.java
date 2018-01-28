package org.ziwenxie.leafer.redis;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.stereotype.Component;

import java.util.Collection;

public class CustomizedRedisCacheManager extends RedisCacheManager {

    private static final Logger logger = LoggerFactory.getLogger(CustomizedRedisCacheManager.class);

    /**
     * 缓存参数的分隔符
     * 数组元素0=缓存的名称
     * 数组元素1=缓存过期时间TTL
     * 数组元素2=缓存在多少秒开始主动失效来强制刷新
     */
    private String separator = "#";

    /**
     * 缓存主动在失效前强制刷新缓存的时间
     * 单位：秒
     */
    private long preloadSecondTime=0;

    public CustomizedRedisCacheManager(RedisOperations redisOperations) {
        super(redisOperations);
    }

    public CustomizedRedisCacheManager(RedisOperations redisOperations, Collection<String> cacheNames) {
        super(redisOperations, cacheNames);
    }

    @Override
    public Cache getCache(String name) {
        String[] cacheParams = name.split(this.getSeparator());
        String cacheName = cacheParams[0];

        if(StringUtils.isBlank(cacheName)){
            return null;
        }

        Long expirationSecondTime = this.computeExpiration(cacheName);

        if(cacheParams.length>1) {
            expirationSecondTime=Long.parseLong(cacheParams[1]);
            this.setDefaultExpiration(expirationSecondTime);
        }

        if(cacheParams.length>2) {
            this.setPreloadSecondTime(Long.parseLong(cacheParams[2]));
        }

        Cache cache = super.getCache(cacheName);

        if(null == cache){
            return cache;
        }

        logger.info("expirationSecondTime:"+expirationSecondTime);

        CustomizedRedisCache redisCache = new CustomizedRedisCache(
                cacheName,
                this.isUsePrefix() ? this.getCachePrefix().prefix(cacheName) : null,
                this.getRedisOperations(),
                expirationSecondTime,
                preloadSecondTime
                );

        return redisCache;
    }

    public long getPreloadSecondTime() {
        return preloadSecondTime;
    }

    public void setPreloadSecondTime(long preloadSecondTime) {
        this.preloadSecondTime = preloadSecondTime;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

}
