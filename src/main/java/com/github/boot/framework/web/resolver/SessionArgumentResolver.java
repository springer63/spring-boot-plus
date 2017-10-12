package com.github.boot.framework.web.resolver;

import com.github.boot.framework.util.ConstUtils;
import com.github.boot.framework.util.JsonUtils;
import com.github.boot.framework.web.annotation.OAuth;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

/**
 * 请求参数解析器
 * @author cjh
 */
public class SessionArgumentResolver implements HandlerMethodArgumentResolver {

    /**
     * 判断是否是支持解析的参数
     * @param parameter
     * @return
     */
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.getParameterAnnotation(OAuth.class) != null;
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
		HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
		Object object = request.getSession().getAttribute(ConstUtils.SESSION_USER);
		if(object instanceof String){
			object = JsonUtils.fromJson(object.toString(), parameter.getParameterType());
		}
		return object;
	}

	
	

}
