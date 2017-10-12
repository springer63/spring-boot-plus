package com.github.boot.framework.jpa;

import java.lang.annotation.*;

/**
 * 查询条件
 * Created by dell on 2017/3/15.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Condition {

    /**
     * 查询条件对应的属性字段
     * @return
     */
    String property() default "";

    /**
     * 条件比较方式
     * @return
     */
    Operator operator() default Operator.EQ;
}
