package com.github.boot.framework.web.result;

import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;

import java.util.*;

/**
 * JSON序列化动态属性过滤器
 *
 * @author chenjianhui
 * @create 2018/01/18
 **/
public class DynamicBeanPropertyFilter extends SimpleBeanPropertyFilter {

    private Map<Class<?>, Set<String>> includePropertyMap;

    private Map<Class<?>, Set<String>> excludePropertyMap;

    public DynamicBeanPropertyFilter(Json[] jsonFilters) {
        for (Json json : jsonFilters){
            if(json.includes().length > 0){
                if(this.includePropertyMap == null){
                    includePropertyMap = new HashMap<>(3);
                }
                includePropertyMap.put(json.type(), new HashSet<>(Arrays.asList(json.includes())));
            }
            if(json.excludes().length > 0){
                if(this.excludePropertyMap == null){
                    excludePropertyMap = new HashMap<>(3);
                }
                excludePropertyMap.put(json.type(), new HashSet<>(Arrays.asList(json.excludes())));
            }
        }
    }

    @Override
    protected boolean include(BeanPropertyWriter writer) {
        Class<?> declaringClass = writer.getMember().getMember().getDeclaringClass();
        if(this.includePropertyMap != null ){
            Set<String> set = includePropertyMap.get(declaringClass);
            if(set != null){
                return set.contains(writer.getName());
            }
        }
        if(this.excludePropertyMap != null){
            Set<String> set = excludePropertyMap.get(declaringClass);
            if(set != null){
                return !set.contains(writer.getName());
            }
        }
        return true;
    }

    @Override
    protected boolean include(PropertyWriter writer) {
        Class<?> declaringClass = writer.getMember().getMember().getDeclaringClass();
        if(this.includePropertyMap != null ){
            Set<String> set = includePropertyMap.get(declaringClass);
            if(set != null){
                return set.contains(writer.getName());
            }
        }
        if(this.excludePropertyMap != null){
            Set<String> set = excludePropertyMap.get(declaringClass);
            if(set != null){
                return !set.contains(writer.getName());
            }
        }
        return true;
    }
}
