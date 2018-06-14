package com.github.boot.framework.web.resolver;

import com.github.boot.framework.web.exception.ApplicationException;
import com.github.boot.framework.web.form.DeleteForm;
import com.github.boot.framework.web.form.GetForm;
import com.github.boot.framework.web.result.Result;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodArgumentResolver;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * GenericArgumentResolver
 *
 * @author chenjianhui
 * @create 2018/05/15
 **/
public class GenericArgumentResolver extends AbstractMessageConverterMethodArgumentResolver {

    public GenericArgumentResolver(List<HttpMessageConverter<?>> converters) {
        super(converters);
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType() == GetForm.class || parameter.getParameterType() == DeleteForm.class;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        ParameterizedType parameterType = (ParameterizedType) parameter.getGenericParameterType();
        Class<?> clazz = (Class<?>) parameterType.getActualTypeArguments()[0];
        Object value;
        GetForm form;
        String contentType = webRequest.getHeader(HttpHeaders.CONTENT_TYPE);
        if(contentType != null && contentType.startsWith(MediaType.APPLICATION_JSON_VALUE)){
            form = (GetForm) this.readWithMessageConverters(webRequest, parameter, parameter.getParameterType());
            value = form.getId();
        }else{
            form = new GetForm<>();
            value = webRequest.getNativeRequest(HttpServletRequest.class).getParameter("id");
        }
        if(value == null){
            throw new ApplicationException(Result.INVALID_PARAM, "参数id不能为空");
        }
        if(Long.class == clazz){
            value = Long.valueOf(value.toString());
        }
        if(Integer.class == clazz){
            value = Integer.valueOf(value.toString());
        }
        form.setId(value);
        return form;
    }
}
