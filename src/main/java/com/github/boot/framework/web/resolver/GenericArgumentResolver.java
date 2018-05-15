package com.github.boot.framework.web.resolver;

import com.github.boot.framework.util.ReflectionUtils;
import com.github.boot.framework.web.exception.ApplicationException;
import com.github.boot.framework.web.form.DeleteForm;
import com.github.boot.framework.web.form.GetForm;
import com.github.boot.framework.web.result.Result;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.reflect.ParameterizedType;

/**
 * GenericArgumentResolver
 *
 * @author chenjianhui
 * @create 2018/05/15
 **/
public class GenericArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType() == GetForm.class || parameter.getParameterType() == DeleteForm.class;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        ParameterizedType parameterType = (ParameterizedType) parameter.getGenericParameterType();
        Class<?> clazz = (Class<?>) parameterType.getActualTypeArguments()[0];
        Object form = parameter.getParameterType().newInstance();
        Object value = webRequest.getParameter("id");
        if(value == null){
            throw new ApplicationException(Result.INVALID_PARAM, "参数id不能为空");
        }
        if(Long.class == clazz){
            value = Long.valueOf(value.toString());
        }
        if(Integer.class == clazz){
            value = Integer.valueOf(value.toString());
        }
        ReflectionUtils.setFieldValue(form, "id", value);
        return form;
    }
}
