package com.github.boot.framework.support.serializer;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created by cjh on 2017/3/29.
 */
public class JacksonSerializer<T> implements Serializer<T> {

    private ObjectMapper objMapper;

    public JacksonSerializer () {
        objMapper = new ObjectMapper();
        objMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        objMapper.enable(JsonParser.Feature.ALLOW_COMMENTS);
        objMapper.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
        objMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
    }

    @Override
    public byte[] serialize(T obj) {
        try {
            return objMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

	@Override
    @SuppressWarnings("unchecked")
    public T deserialize(byte[] bytes) {
        try { 
            return (T) objMapper.readValue(bytes, Object.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } 
    }


}
