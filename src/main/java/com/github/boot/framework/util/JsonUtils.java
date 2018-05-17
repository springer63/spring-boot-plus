package com.github.boot.framework.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

/**
 * 依赖Jackson框架封装的工具类
 * 用于Object与JSON，XML之间的互转
 * @author chenjianhui
 */
public class JsonUtils {
	
	private static ObjectMapper objMapper;
	
	private static XmlMapper xmlMapper;

	static{
		objMapper = new ObjectMapper();
		Hibernate5Module hm = new Hibernate5Module();
		hm.configure(Hibernate5Module.Feature.USE_TRANSIENT_ANNOTATION, false);
		objMapper.registerModule(new ParameterNamesModule());
		objMapper.registerModule(new Jdk8Module());
		objMapper.registerModule(new JavaTimeModule());
		objMapper.registerModule(hm);
		objMapper.setSerializationInclusion(Include.NON_NULL);
		objMapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
		JacksonXmlModule module = new JacksonXmlModule();
		module.setDefaultUseWrapper(false);
		xmlMapper = new XmlMapper(module);
		xmlMapper.setSerializationInclusion(Include.NON_DEFAULT);
		xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	

	/**
	 * Object to JSON
	 * @param obj
	 * @return
	 * @throws JsonProcessingException 
	 */
	public static String toJson(Object obj){
		if(obj != null) {
			try {
				return objMapper.writeValueAsString(obj);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * JSON to Object
	 * @param json
	 * @param clazz
	 * @return
	 */
	public static <T> T fromJson(String json, Class<T> clazz) {
		if(ValidUtils.isValid(json)) {
			try {
				return objMapper.readValue(json, clazz);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	
	/**
	 * Object to XML
	 * @param object
	 * @return
	 */
	public static String toXml(Object object) {
		try {
			return xmlMapper.writeValueAsString(object);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * XML to Object
	 * @param xmlStr
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	public static <T> T fromXml(String xmlStr, Class<T> clazz) throws Exception{
		if(ValidUtils.isValid(xmlStr)){
			return xmlMapper.readValue(xmlStr, clazz);
		}
		return null;
	}

}
