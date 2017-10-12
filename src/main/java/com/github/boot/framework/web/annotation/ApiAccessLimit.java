package com.github.boot.framework.web.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by cjh on 2017/4/17.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiAccessLimit {

    /**
     * 一段时间内的访问次数
     */
    int frequency();

    /**
     * 时间间隔
     */
    int interval();

    /**
     * 超过访问频率后, 锁定时间, 默认1小时
     * @return
     */
    int lockTime() default 3600;

    /**
     * 限制类型
     * @return
     */
    int type() default 1; //1:用户, 2:IP

    /**
     * 时间单位
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;



}
