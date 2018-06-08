package com.github.boot.framework.web.result;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * 自定义JSON序列化器
 *
 * @author chenjianhui
 * @date 2017/2/27
 */
public class ResultJsonSerializer extends ObjectMapper {

	private static final long serialVersionUID = -4507425084024099525L;

    public ResultJsonSerializer() {
        this.registerModule(new ParameterNamesModule());
        this.registerModule(new Jdk8Module());
        this.registerModule(new JavaTimeModule());
        SimpleModule module = new SimpleModule();
        module.addSerializer(BigDecimal.class, new ToStringSerializer(){
            @Override
            public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
                BigDecimal v = (BigDecimal) value;
                gen.writeString(v.toPlainString());
            }
        });
        this.registerModule(module);
        //不序列化空对象
        this.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        this.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        this.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //支持JSON格式提交可以多字段或少字段
        this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.addMixIn(Object.class, DynamicFilterMixIn.class);
        this.setFilterProvider(new DynamicFilterProvider());


    }

    @JsonFilter(DynamicFilterProvider.FILTER_ID)
    interface DynamicFilterMixIn {
    }

}
