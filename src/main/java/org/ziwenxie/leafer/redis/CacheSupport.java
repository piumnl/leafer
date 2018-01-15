package org.ziwenxie.leafer.redis;

public interface CacheSupport {

    /**
     * 刷新容器中的所有键
     * @param cacheName
     */
    void refreshCache(String cacheName);

    /**
     * 按照容器以及给定的键刷新
     * @param cacheName
     * @param cacheKey
     */
    void refreshCacheByKey(String cacheName,String cacheKey);

}
