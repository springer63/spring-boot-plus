package com.github.boot.framework.support.validate;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Date;

/**
 * 后置时间验证器，验证某个时间必须在某个时间之后
 * @author ChenJianhui
 */
public class RearDateValidator implements ConstraintValidator<RearDate, Date>{
	
	private RearDate rearDate;
	

	@Override
	public void initialize(RearDate rearDate) {
		this.rearDate = rearDate;
	}

	@Override
	public boolean isValid(Date date, ConstraintValidatorContext context) {
		if(date.getTime() >= rearDate.frontTime()){
			return true;
		}else{
            String messageTemplate = context.getDefaultConstraintMessageTemplate();  
            context.disableDefaultConstraintViolation();  
            context.buildConstraintViolationWithTemplate(messageTemplate).addPropertyNode("frontTime").addConstraintViolation();  
            return false;
		}
	}

}
