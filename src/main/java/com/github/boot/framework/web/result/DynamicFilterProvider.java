package com.github.boot.framework.web.result;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import static java.util.Objects.requireNonNull;

/**
 * 动态过滤器提供者
 *
 * @author chenjianhui
 * @create 2018/01/18
 **/
public class DynamicFilterProvider extends SimpleFilterProvider implements PropertyFilter {

    public static final String FILTER_ID = "DynamicFilterProvider$FILTER";

    private static final long serialVersionUID = -362486406389944351L;

    private final PropertyFilter delegate;

    public DynamicFilterProvider() {
        this(SimpleBeanPropertyFilter.serializeAll());
    }

    public DynamicFilterProvider(PropertyFilter delegate) {
        this.delegate = requireNonNull(delegate);
        addFilter(FILTER_ID, this);
    }

    @Override
    public void serializeAsField(Object pojo, JsonGenerator jgen, SerializerProvider prov,
                                 PropertyWriter writer) throws Exception {
        delegate.serializeAsField(pojo, jgen, prov, writer);
    }

    @Override
    public void serializeAsElement(Object elementValue, JsonGenerator jgen, SerializerProvider prov,
                                   PropertyWriter writer) throws Exception {
        delegate.serializeAsElement(elementValue, jgen, prov, writer);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void depositSchemaProperty(PropertyWriter writer, ObjectNode propertiesNode,
                                      SerializerProvider provider) throws JsonMappingException {
        delegate.depositSchemaProperty(writer, propertiesNode, provider);
    }

    @Override
    public void depositSchemaProperty(PropertyWriter writer, JsonObjectFormatVisitor objectVisitor,
                                      SerializerProvider provider) throws JsonMappingException {
        delegate.depositSchemaProperty(writer, objectVisitor, provider);
    }

}

