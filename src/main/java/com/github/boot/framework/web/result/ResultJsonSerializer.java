package com.github.boot.framework.web.result;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

/**
 * 自定义JSON序列化器
 * Created by cjh on 2017/2/27.
 */
public class ResultJsonSerializer extends ObjectMapper {

	private static final long serialVersionUID = -4507425084024099525L;

	static final String DYNC_INCLUDE = "DYNC_INCLUDE";

    static final String DYNC_EXCLUDE = "DYNC_EXCLLUDE";

    public ResultJsonSerializer() {
        //Hibernate5Module hm = new Hibernate5Module();
        //hm.configure(Hibernate5Module.Feature.USE_TRANSIENT_ANNOTATION, false);
        //this.registerModule(hm);
        this.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        this.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        this.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //支持JSON格式提交可以多字段或少字段
        this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @JsonFilter(DYNC_EXCLUDE)
    interface DynamicExclude {
    }

    @JsonFilter(DYNC_INCLUDE)
    interface DynamicInclude {
    }

    /**
     * @param clazz   需要设置规则的Class
     * @param includes 转换时包含哪些字段
     * @param excludes 转换时过滤哪些字段
     */
    public void filter(Class<?> clazz, String[] includes, String[] excludes) {
        if (clazz == null) return;
        if (includes != null && includes.length > 0) {
            this.setFilterProvider(new SimpleFilterProvider().addFilter(DYNC_INCLUDE,
                    SimpleBeanPropertyFilter.filterOutAllExcept(includes)));
            this.addMixIn(clazz, DynamicInclude.class);
        } else if (excludes != null && excludes.length > 0) {
            this.setFilterProvider(new SimpleFilterProvider().addFilter(DYNC_EXCLUDE,
                    SimpleBeanPropertyFilter.serializeAllExcept(excludes)));
            this.addMixIn(clazz, DynamicExclude.class);
        }
    }
}
