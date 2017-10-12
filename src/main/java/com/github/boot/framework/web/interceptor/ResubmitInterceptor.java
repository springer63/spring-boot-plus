package com.github.boot.framework.web.interceptor;

import com.github.boot.framework.util.ConstUtils;
import com.github.boot.framework.util.ValidUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 防止请求重复提交拦截器
 * @author ChenJianhui
 */
public class ResubmitInterceptor extends HandlerInterceptorAdapter{
	
	private static final Logger log = LoggerFactory.getLogger(ResubmitInterceptor.class);
	
	/**
	 * 防止请求重复提交
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		HttpSession session = request.getSession();
		String requestId = request.getParameter("t");
		if(!ValidUtils.isValid(requestId)){
			return true;
		}
		requestId = request.getRequestURI() + request.getParameter("t");
		String sessionRequestId = (String) session.getAttribute(ConstUtils.SESSION_API_REQUESTID);
		session.setAttribute(ConstUtils.SESSION_API_REQUESTID, requestId);
		if(requestId.equals(sessionRequestId)){
			log.error("重复提交的请求: " + requestId);
			return false;
		}
		return true;
	}

}
