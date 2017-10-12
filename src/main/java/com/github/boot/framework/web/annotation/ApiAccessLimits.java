package com.github.boot.framework.web.annotation;

import java.lang.annotation.*;

/**
 * Created by cjh on 2017/4/17.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiAccessLimits {

   ApiAccessLimit[] value();

}
