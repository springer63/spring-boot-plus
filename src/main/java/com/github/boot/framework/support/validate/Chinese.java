package com.github.boot.framework.support.validate;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 验证字符串是否是中文
 * @author ChenJianhui
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Constraint(validatedBy = ChineseValidator.class)
public @interface Chinese {

	String message() default "非中文字符";
    Class<?>[] groups() default {};  
    Class<? extends Payload>[] payload() default {};  
  
}
