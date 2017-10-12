package com.github.boot.framework.support.validate;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;


/**
 * 校验URL的有效性
 * @author ChenJianhui
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Constraint(validatedBy = UrlValidator.class)
public @interface URL {

	String message() default "URL格式不正确";  
    Class<?>[] groups() default {};  
    Class<? extends Payload>[] payload() default {};  
  
}
