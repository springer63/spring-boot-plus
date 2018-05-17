package com.github.boot.framework.support.cache;

import java.lang.annotation.*;

/**
 * CacheTimes
 *
 * @author chenjianhui
 * @create 2018/05/17
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface CacheTimes {

    CacheTime[] value();
}
