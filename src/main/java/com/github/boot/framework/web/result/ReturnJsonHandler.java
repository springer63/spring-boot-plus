package com.github.boot.framework.web.result;

import com.github.boot.framework.util.ConstUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Json 视图动态过滤
 *
 * @author cjh
 * @date 2017/2/27
 */
public class ReturnJsonHandler implements HandlerMethodReturnValueHandler{

    private static final ResultJsonSerializer DEFAULT_RESULT_SERIALIZER = new ResultJsonSerializer();

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return returnType.getMethod().getReturnType() == Result.class;
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        mavContainer.setRequestHandled(true);
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        Json json = returnType.getMethodAnnotation(Json.class);
        ResultJsonSerializer jsonSerializer = DEFAULT_RESULT_SERIALIZER;
        if(json != null){
            jsonSerializer = new ResultJsonSerializer();
            jsonSerializer.filter(json.type(), json.includes(), json.excludes());
        }
        Result result = (Result) returnValue;
        result.setUserId(webRequest.getNativeRequest(HttpServletRequest.class).getSession().getAttribute(ConstUtils.SESSION_USER_ID));
        result.setTimestamp(System.currentTimeMillis());
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        String jsonStr = jsonSerializer.writeValueAsString(returnValue);
        response.getWriter().write(jsonStr);
    }
}

