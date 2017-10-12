package com.github.boot.framework.jpa;

import java.lang.annotation.*;

/**
 * Created by cjh on 2017/3/29.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface NativeQuery{

   /**
    * SQL语句
    * @return
    */
   String value();

   /**
    * 返回结果类型
    * @return
    */
   Class<?> resultType() default Void.class;

}
