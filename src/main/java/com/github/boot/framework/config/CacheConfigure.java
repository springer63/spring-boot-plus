package com.github.boot.framework.config;

import com.github.boot.framework.support.cache.CacheKeyGenerator;
import com.github.boot.framework.support.cache.CacheTime;
import com.github.boot.framework.util.PackageUtils;
import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 缓存配置
 * Created by cjh on 2017/3/12.
 */
@Configurable
@EnableCaching
public class CacheConfigure extends CachingConfigurerSupport {

    @Value("${spring.cache.packages}")
    private String cachePackges;

    @Override
    public KeyGenerator keyGenerator() {
        return new CacheKeyGenerator();
    }

    @Bean
    public CacheManager cacheManager(RedissonClient redissonClient) throws Exception{
        Set<Class<?>> classes = new PackageUtils(cachePackges.split(","), org.springframework.cache.annotation.CacheConfig.class).scan();
        Map<String, CacheConfig> configMap = new HashMap<String, CacheConfig>(classes.size());
        CacheConfig baseConfig = new CacheConfig(24 * 60 * 1000, 12 * 60 * 1000);
        for (Class<?> clazz: classes){
            CacheConfig cacheConfig = baseConfig;
            CacheTime cacheTime = clazz.getAnnotation(CacheTime.class);
            if(cacheTime != null){
                cacheConfig = new CacheConfig(cacheTime.ttl(), cacheTime.tti());
            }
            org.springframework.cache.annotation.CacheConfig annotation = clazz.getAnnotation(org.springframework.cache.annotation.CacheConfig.class);
            configMap.put(annotation.cacheNames()[0], cacheConfig);
        }
        return new RedissonSpringCacheManager(redissonClient, configMap);
    }


}
