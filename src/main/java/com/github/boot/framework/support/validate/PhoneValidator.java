package com.github.boot.framework.support.validate;



import com.github.boot.framework.util.ValidUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 手机号验证器
 * @author ChenJianhui
 */
public class PhoneValidator implements ConstraintValidator<Phone, String>{

	@Override
	public void initialize(Phone phone) {
		
	}

	@Override
	public boolean isValid(String phone, ConstraintValidatorContext context) {
		if(!ValidUtils.isValid(phone)){
			return true;
		}else{
			String regExp = "((13|18)\\d{9})|((145|147|150|151|152|153|155|156|157|158|159|176)\\d{8})|((1700|1705|1709)\\d{7})";
			Pattern p = Pattern.compile(regExp);  
			Matcher m = p.matcher(phone); 
			return m.find();
		}
	}
	
}
