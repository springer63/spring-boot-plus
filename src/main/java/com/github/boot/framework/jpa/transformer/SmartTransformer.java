package com.github.boot.framework.jpa.transformer;

import org.hibernate.transform.BasicTransformerAdapter;
import org.springframework.core.convert.support.DefaultConversionService;

import java.util.List;

/**
 * 基本类型转换器
 * Created by cjh on 2017/7/15.
 */
public class SmartTransformer extends BasicTransformerAdapter {

	private static final long serialVersionUID = -6487683648769621742L;

	private static DefaultConversionService conversionService = new DefaultConversionService();

	private final Class<?> clazz;

	public SmartTransformer(Class<?> clazz) {
		this.clazz = clazz;
	}

	@Override
	public Object transformTuple(Object[] tuple, String[] aliases) {
		if (tuple != null && tuple.length > 0) {
			return conversionService.convert(tuple[0], clazz);
		}
		return null;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public List<?> transformList(List list) {
		return super.transformList(list);
	}
}
