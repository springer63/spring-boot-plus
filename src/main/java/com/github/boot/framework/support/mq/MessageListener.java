package com.github.boot.framework.support.mq;

/**
 * 消息监听器
 * @author cjh
 * @version 1.0
 */
public interface MessageListener<T extends AbstractMessage> {
	
	/**
	 * 接收并处理订阅消息
	 * @param message 消息
	 */
	void onMessage(T message);

}

