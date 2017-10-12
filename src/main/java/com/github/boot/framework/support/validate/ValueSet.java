package com.github.boot.framework.support.validate;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 验证参数在某个集合范围内
 * @author ChenJianhui
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Constraint(validatedBy = ValueSetValidator.class)
public @interface ValueSet {

	/**
	 * 验证失败错误信息
	 * @return
	 */
	String message() default "输入的参数不在{value}中";
	
	/**
	 * 值集合
	 * @return
	 */
	String[] value();
	
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};  
  
}
