package com.github.boot.framework.web.resolver;

import com.github.boot.framework.util.JsonUtils;
import com.github.boot.framework.util.ReflectionUtils;
import com.github.boot.framework.web.exception.ApplicationException;
import com.github.boot.framework.web.form.Form;
import com.github.boot.framework.web.form.GetForm;
import com.github.boot.framework.web.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodArgumentResolver;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 请求参数解析器
 * @author cjh
 * @date 2017/06/24
 */
public class FormArgumentResolver extends AbstractMessageConverterMethodArgumentResolver {

	private static final Logger logger = LoggerFactory.getLogger(FormArgumentResolver.class);

	public FormArgumentResolver(List<HttpMessageConverter<?>> converters) {
		super(converters);
	}

    /**
     * 判断是否是支持解析的参数
     * @param parameter
     * @return
     */
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return ReflectionUtils.isImplement(parameter.getParameterType(), Form.class);
	}

    /**
     * 解析参数
     * @param parameter
     * @param mavContainer
     * @param webRequest
     * @param binderFactory
     * @return
     * @throws Exception
     */
	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
			WebDataBinderFactory binderFactory) throws Exception {
		Object value = null;
		String contentType = webRequest.getHeader(HttpHeaders.CONTENT_TYPE);
		if(contentType != null && contentType.startsWith(MediaType.APPLICATION_JSON_VALUE)){
			value = this.readWithMessageConverters(webRequest, parameter, parameter.getParameterType());
		}
		if(value == null){
			value = parameter.getParameterType().newInstance();
		}
		ServletRequestDataBinder binder = (ServletRequestDataBinder) binderFactory.createBinder(webRequest, value, parameter.getParameterName());
		binder.bind(webRequest.getNativeRequest(ServletRequest.class));
		binder.validate();
		BindingResult br = binder.getBindingResult();
		if(br.getFieldErrorCount() > 0){
			String errorMsg;
			if(br.getFieldError().isBindingFailure() ){
				errorMsg = String.format("参数[%s]类型不匹配", br.getFieldError().getField());
			}else{
				errorMsg = br.getFieldError().getField() + " " + br.getFieldError().getDefaultMessage();
			}
			logger.info("接口【{}】参数校验失败：{}", webRequest.getNativeRequest(HttpServletRequest.class).getRequestURI(), br.getFieldError().getDefaultMessage());
			throw new ApplicationException(Result.INVALID_PARAM, errorMsg);
		}
		return value;
	}

	public static void main(String[] args) {
		GetForm<Long> getForm = JsonUtils.fromJson("{\"id\": 15}", GetForm.class);
		System.out.println(getForm.getId().getClass());
	}


}
