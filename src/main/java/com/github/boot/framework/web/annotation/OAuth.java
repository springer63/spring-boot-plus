package com.github.boot.framework.web.annotation;

import java.lang.annotation.*;

/**
 * Created by cjh on 2017/3/15.
 */
@Target({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface OAuth {

    /**
     * 是否需要授权
     * @return
     */
    boolean required() default true;

}
