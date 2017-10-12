package com.github.boot.framework.web.interceptor;

import com.github.boot.framework.util.ConstUtils;
import com.github.boot.framework.util.JsonUtils;
import com.github.boot.framework.web.result.Result;
import com.github.boot.framework.web.annotation.OAuth;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

/**
 * 资源权限控制拦截器
 * Created by cjh on 2017/9/14.
 */
public class SecurityInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(!(handler instanceof HandlerMethod)){
            return true;
        }
        HandlerMethod method = (HandlerMethod) handler;
        OAuth oAuth = method.getMethodAnnotation(OAuth.class);
        if(oAuth != null && !oAuth.required()){
            return true;
        }
        if(request.getSession().getAttribute(ConstUtils.SESSION_USER) == null){
            request.getRequestDispatcher("/error/" + Result.NOT_OAUTH).forward(request, response);
            return false;
        }
        Set<String> allResources = (Set<String>) request.getSession().getServletContext().getAttribute(ConstUtils.APP_ALLOW_URLS);
        if( allResources != null && !allResources.contains(request.getRequestURI())){
            return true;
        }
        String jsonStr = (String) request.getSession().getAttribute(ConstUtils.SESSION_USER_ALLOW_RIGHT);
        Set<String> resources = JsonUtils.fromJson(jsonStr, Set.class);
        if(!resources.contains(request.getRequestURI())){
            request.getRequestDispatcher("/error/" + Result.PERMISSION_DENIED).forward(request, response);
            return false;
        }
        return true;
    }


}
