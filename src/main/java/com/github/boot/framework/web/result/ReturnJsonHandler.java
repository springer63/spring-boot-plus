package com.github.boot.framework.web.result;

import com.github.boot.framework.util.ConstUtils;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;

/**
 * Json 视图动态过滤
 * Created by cjh on 2017/2/27.
 */
public class ReturnJsonHandler implements HandlerMethodReturnValueHandler, Ordered {

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return returnType.getMethod().getReturnType() == Result.class;
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        mavContainer.setRequestHandled(true);
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        Annotation[] annos = returnType.getMethodAnnotations();
        ResultJsonSerializer jsonSerializer = new ResultJsonSerializer();
        for (Annotation a : annos) {
            if (! (a instanceof Json)) {
                continue;
            }
            Json json = (Json) a;
            jsonSerializer.filter(json.type(), json.includes(), json.excludes());
        }
        Result result = (Result) returnValue;
        result.setUserId(webRequest.getNativeRequest(HttpServletRequest.class).getSession().getAttribute(ConstUtils.SESSION_USER_ID));
        result.setHandleTime(System.currentTimeMillis());
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        String json = jsonSerializer.writeValueAsString(returnValue);
        response.getWriter().write(json);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}

