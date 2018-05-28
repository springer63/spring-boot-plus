package com.github.boot.framework.jpa;

import java.lang.annotation.*;

/**
 * 指定排序属性注解
 *
 * @author chenjianhui
 * @create 2018/05/09
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface SortProperty {

    /**
     * 属性名称
     * @return
     */
    String value() default "";

    /**
     * 排序方向
     * @return
     */
    String direction() default "DESC";


}
