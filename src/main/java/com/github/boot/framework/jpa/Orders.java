package com.github.boot.framework.jpa;


import java.lang.annotation.*;

/**
 * 分页排序
 * Created by cjh on 2017/3/15.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Orders {
    Order[] value();
}
