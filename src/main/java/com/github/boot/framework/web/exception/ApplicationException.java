package com.github.boot.framework.web.exception;

/**
 * @Description 自定义系统异常
 * @author cjh
 * @version 1.0
 * @date：2017年2月15日 下午8:04:41
 */
public class ApplicationException extends RuntimeException{

	private static final long serialVersionUID = -7883840067913852752L;

	private int code;

	public ApplicationException(int code, String message) {
		super(message);
		this.code = code;
	}

	public int getCode() {
		return code;
	}


}
