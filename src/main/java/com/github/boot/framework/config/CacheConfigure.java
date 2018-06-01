package com.github.boot.framework.config;

import com.github.boot.framework.support.cache.CacheKeyGenerator;
import com.github.boot.framework.support.cache.CacheTime;
import com.github.boot.framework.util.PackageUtils;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.CompositeCodec;
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
 *
 * @author cjh
 * @date 2017/3/12
 */
@Configurable
@EnableCaching
public class CacheConfigure extends CachingConfigurerSupport {

    @Value("${spring.cache.packages}")
    private String cachePackages;

    @Override
    public KeyGenerator keyGenerator() {
        return new CacheKeyGenerator();
    }

    @Bean
    public CacheManager cacheManager(RedissonClient redissonClient) throws Exception{
        Set<Class<?>> classes = new PackageUtils(cachePackages.split(","), org.springframework.cache.annotation.CacheConfig.class).scan();
        Map<String, CacheConfig> configMap = new HashMap<>(classes.size());
        CacheConfig baseConfig = new CacheConfig(24 * 60 * 60 * 1000, 12 * 60 * 60 * 1000);
        for (Class<?> clazz: classes){
            CacheTime[] cacheTimes = clazz.getAnnotationsByType(CacheTime.class);
            Map<String, CacheConfig> cacheTimeMap = new HashMap<>(5);
            if(cacheTimes != null && cacheTimes.length > 0){
                for (CacheTime t : cacheTimes){
                    CacheConfig cacheConfig = new CacheConfig(t.ttl(), t.tti());
                    cacheTimeMap.put(t.cacheName(), cacheConfig);
                }
            }
            org.springframework.cache.annotation.CacheConfig config = clazz.getAnnotation(org.springframework.cache.annotation.CacheConfig.class);
            for (String name : config.cacheNames()){
                CacheConfig cacheConfig = cacheTimeMap.get(name);
                if(cacheConfig == null){
                    cacheConfig = baseConfig;
                }
                configMap.put(name, cacheConfig);
            }
        }

        RedissonSpringCacheManager  cacheManager = new RedissonSpringCacheManager(redissonClient, configMap);
        RedisConfigure.CacheKryoCodec valueCode = new RedisConfigure.CacheKryoCodec();
        cacheManager.setCodec(new CompositeCodec(new StringCodec(), valueCode, valueCode));
        return cacheManager;
    }


}
