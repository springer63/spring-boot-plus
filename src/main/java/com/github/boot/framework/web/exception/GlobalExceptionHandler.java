package com.github.boot.framework.web.exception;

import com.github.boot.framework.web.result.Result;
import com.github.boot.framework.web.annotation.OAuth;
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
import org.springframework.ui.ModelMap;
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

	private final static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	private ErrorAttributes errorAttributes;

	@Autowired
	public GlobalExceptionHandler(ErrorAttributes errorAttributes) {
		super(errorAttributes, new ErrorProperties());
		this.errorAttributes = errorAttributes;
	}

	@OAuth(required = false)
	@RequestMapping("/" + Result.NOT_OAUTH)
	public Result unauthorized(){
		return Result.unauthorized();
	}

	@OAuth(required = false)
	@RequestMapping("/" + Result.PERMISSION_DENIED)
	public Result permissionDenied(){
		return Result.permissionDenied();
	}

	@OAuth(required = false)
	@RequestMapping("/" + Result.INVALID_PARAM)
	public Result invalidParam(ModelMap modelMap){
		return new Result(Result.INVALID_PARAM, modelMap.get("message").toString());
	}

	@OAuth(required = false)
	@RequestMapping("/" + Result.SYSTEM_BUSY)
	public Result systemBusy() {
		return Result.systemBusy();
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
		Result result = Result.systemError();
		if(HttpStatus.NOT_FOUND.equals(status)) {
			result = Result.notFound();
		}
		RequestAttributes requestAttributes = new ServletRequestAttributes(request);
		Throwable throwable = this.errorAttributes.getError(requestAttributes);
		if(throwable == null){
			return new ResponseEntity<>(result, HttpStatus.OK);
		}
		if(throwable instanceof ApplicationException){
			ApplicationException ex = (ApplicationException) throwable;
			result.setStatus(ex.getCode());
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