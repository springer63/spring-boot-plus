package com.github.boot.framework.support.validate;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * 验证时间参数必需在某个时间参数后面的时间点
 * @author ChenJianhui
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Constraint(validatedBy = RearDateValidator.class)
public @interface RearDate {

	
	String message() default "时间必须在{frontFieldName}之后";  
    Class<?>[] groups() default {};  
    Class<? extends Payload>[] payload() default {};  
  
    /**
     * 前置时间
     * @return
     */
    long frontTime() ;
}
