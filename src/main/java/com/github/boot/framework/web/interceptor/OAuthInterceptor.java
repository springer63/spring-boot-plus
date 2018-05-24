package com.github.boot.framework.web.interceptor;

import com.github.boot.framework.util.ConstUtils;
import com.github.boot.framework.web.exception.GlobalExceptionHandler;
import com.github.boot.framework.web.result.Result;
import com.github.boot.framework.web.annotation.OAuth;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 权限过滤拦截器
 * @author ChenJianhui
 * @date 2017/6/19
 */
public class OAuthInterceptor extends HandlerInterceptorAdapter{
	
	/**
	 * 权限过滤
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if(!(handler instanceof HandlerMethod)){
			return true;
		}
		HandlerMethod method = (HandlerMethod) handler;
		OAuth oAuth = method.getMethodAnnotation(OAuth.class);
		if(oAuth == null || !oAuth.required()){
			return true;
		}
		if(request.getSession().getAttribute(ConstUtils.SESSION_USER_ID) == null){
			request.setAttribute(GlobalExceptionHandler.ERROR_RESULT, Result.unauthorized());
			request.getRequestDispatcher(GlobalExceptionHandler.ERROR_URI).forward(request, response);
			return false;
		}
		return true;
	}

}