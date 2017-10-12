package com.github.boot.framework.jpa;


import org.springframework.data.domain.Sort;

import java.lang.annotation.*;

/**
 * 分页排序
 * Created by cjh on 2017/3/15.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Order {

    /**
     * 排序字段
     * @return
     */
    String orderBy();

    /**
     * 排序方向
     * @return
     */
    Sort.Direction direction () default Sort.Direction.DESC;
}
