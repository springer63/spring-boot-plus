package com.github.boot.framework.support.mq;

/**
 * 消息生成者接口
 * @author cjh
 * @version 1.0
 */
public interface MessageProducer {
	
	/**
	 * 同步发送消息，只要不抛异常就表示成功
	 * @param message 消息
	 */
	void send(Message message);

	/**
	 * 发送异步消息，异步Callback形式
	 * @param message 消息
	 */
	 void sendAsync(Message message);
	 
}
