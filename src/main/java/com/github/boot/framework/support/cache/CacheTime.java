package com.github.boot.framework.support.cache;

import java.lang.annotation.*;

/**
 * 缓存时间设置
 * Created by cjh on 2017/3/16.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Repeatable(CacheTimes.class)
public @interface CacheTime{

    /**
     * TTL（Time To Live ）毫秒为单位
     * 存活期，即从缓存中创建时间点开始直到它到期的一个时间段
     * 不管在这个时间段内有没有访问都将过期
     * @return
     */
    long ttl();

    /**
     * TTI（Time To Idle）毫秒为单位
     * 空闲期，即一个数据多久没被访问将从缓存中移除的时间。
     * @return
     */
    long tti();

    /**
     * 缓存名称
     * @return
     */
    String cacheName() default "*";
}
