package com.github.boot.framework.support.cache;

import com.github.boot.framework.util.ReflectionUtils;
import org.springframework.cache.interceptor.KeyGenerator;

import java.lang.reflect.Method;

/**
 * 自定义缓存KEY生成器
 * Created by cjh on 2017/3/16.
 */
public class CacheKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        StringBuffer buffer = new StringBuffer(method.getName()).append(":");
        for (Object arg : params){
            if(arg == null){
                continue;
            }
            if(ReflectionUtils.isComplexType(arg.getClass())){
                buffer.append(ReflectionUtils.toString(arg));
            }else{
                buffer.append(arg);
            }
        }
        return buffer.toString();
    }

}
