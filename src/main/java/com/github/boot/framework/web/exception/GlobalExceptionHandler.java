package com.github.boot.framework.web.exception;

import com.github.boot.framework.web.annotation.OAuth;
import com.github.boot.framework.web.result.Result;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.BasicErrorController;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description 全局异常处理类
 * @author cjh
 * @version 1.0
 * @date：2017年2月15日 下午8:23:51
 */
@Controller
@ControllerAdvice
@RequestMapping("/error")
public class GlobalExceptionHandler extends BasicErrorController {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	/**
	 * 错误处理地址
	 */
	public static final String ERROR_URI = "/error/500";

	/**
	 * 错误结果
	 */
	public static final String ERROR_RESULT = "$errorResult";

	private ErrorAttributes errorAttributes;

	@Autowired
	public GlobalExceptionHandler(ErrorAttributes errorAttributes) {
		super(errorAttributes, new ErrorProperties());
		this.errorAttributes = errorAttributes;
	}

	/**
	 * 系统全局异常处理
	 * @param request
	 * @return
	 */
	@ResponseBody
	@OAuth(required = false)
	@RequestMapping(value = {"/500", ""}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Result> err(HttpServletRequest request) {
		HttpStatus status = getStatus(request);
		Result result;
		if(HttpStatus.NOT_FOUND.equals(status)) {
			result = Result.notFound();
			return new ResponseEntity<>(result, HttpStatus.OK);
		}
		RequestAttributes requestAttributes = new ServletRequestAttributes(request);
		result = (Result) request.getAttribute(ERROR_RESULT);
		if(result != null){
			return new ResponseEntity<>(result, HttpStatus.OK);
		}
		Throwable throwable = this.errorAttributes.getError(requestAttributes);
		if(throwable == null){
			return new ResponseEntity<>(result, HttpStatus.OK);
		}
		if(throwable instanceof ApplicationException){
			ApplicationException ex = (ApplicationException) throwable;
			result.setCode(ex.getCode());
			result.setMessage(ex.getMessage());
			logger.info("{} at {}", ex.getMessage(), ex.getCause().getStackTrace()[0].toString());
		}else {
			logger.error("未知异常：" + ExceptionUtils.getFullStackTrace(throwable));
		}
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	/**
	 * Controller层全局异常处理
	 * @param e
	 * @return
	 */
	@ExceptionHandler(Exception.class)
	public Result handleException(Exception e) {
		if(e instanceof ApplicationException){
			logger.info("{} at {}", e.getMessage(), e.getStackTrace()[0].toString());
			ApplicationException ex = (ApplicationException) e;
			return new Result(ex.getCode(), ex.getMessage());
		}
		if(e instanceof HttpRequestMethodNotSupportedException){
			return new Result(Result.INVALID_REQUEST_METHOD, e.getMessage());
		}
		logger.error(ExceptionUtils.getFullStackTrace(e));
		return Result.systemError();
	}

}