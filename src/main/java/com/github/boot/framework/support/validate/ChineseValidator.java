package com.github.boot.framework.support.validate;

import com.github.boot.framework.util.StringUtils;
import com.github.boot.framework.util.ValidUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 手机号验证器
 * @author ChenJianhui
 */
public class ChineseValidator implements ConstraintValidator<Chinese, String> {

	@Override
	public void initialize(Chinese chinese) {
		
	}

	@Override
	public boolean isValid(String str, ConstraintValidatorContext context) {
		if(!ValidUtils.isValid(str)){
			return true;
		}
		return StringUtils.isChinese(str);
	}

}
