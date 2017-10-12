package com.github.boot.framework.support.mq;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 消息主题
 * 设置对应的消息实体类上
 * @author cjh
 * @version 1.0
 */
@Retention(RetentionPolicy.RUNTIME) 
@Target({ElementType.TYPE})
@Documented
public @interface MessageTopic {

	/**
	 * 消息主题
	 * @return
	 */
	String value();

	/**
	 * 消息标签
	 * @return
	 */
	String tag() default "*";

	/**
	 * 消费模式
	 * @return
	 */
	MessageModel model() default MessageModel.CLUSTERING;

}
