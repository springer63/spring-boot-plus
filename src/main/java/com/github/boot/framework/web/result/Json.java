package com.github.boot.framework.web.result;

import java.lang.annotation.*;

/**
 * JSON动态视图标识注解
 * Created by cjh on 2017/2/27.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Jsons.class)
public @interface Json {

    /**
     * 过滤对象类型
     *
     * @return
     */
    Class<?> type();

    /**
     * 包含字段
     *
     * @return
     */
    String[] includes() default {};

    /**
     * 排除字段
     *
     * @return
     */
    String[] excludes() default {};
}
