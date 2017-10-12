package com.github.boot.framework.support.validate;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import org.hibernate.validator.constraints.Length;


/**
 * 验证手机号的有效性
 * @author ChenJianhui
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Length(min = 11, max = 11)
@Constraint(validatedBy = PhoneValidator.class)
public @interface Phone {

	String message() default "手机号格式不正确";  
    Class<?>[] groups() default {};  
    Class<? extends Payload>[] payload() default {};  
  
}
