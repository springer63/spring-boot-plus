/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.github.boot.framework.support.serializer;

/**
 * 对象序列化接口
 * Created by cjh on 2017/3/28.
 */
public interface Serializer<T> {

	/**
	 * 序列化
	 * @param obj
	 * @return
	 */
	byte[] serialize(T obj) ;

	/**
	 * 反序列化
	 * @param bytes
	 * @return
	 */
	T deserialize(byte[] bytes) ;
}
