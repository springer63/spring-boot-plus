package com.github.boot.framework.support.mq;

import java.io.Serializable;

/**
 * 消息实体
 * @author cjh
 * @version 1.0
 */
public abstract class AbstractMessage implements Serializable{
	
	private static final long serialVersionUID = 5550172143186489174L;
	
	/**
	 * 消息唯一标识， 防止消息重复投递
	 */
	private String key;

	/**
	 * 根据消息体类型获取消息频道
	 * @return
	 */
	public String topic(){
		MessageTopic topic = this.getClass().getAnnotation(MessageTopic.class);
		if(topic == null){
			throw new RuntimeException(this.getClass().getName() + "消息体没有指定发送的TOPIC");
		}
		return topic.value();
	}

	public String getKey() {
		return this.key;
	}

	public void setKey(String key) {
		this.key = key;
	}

}
