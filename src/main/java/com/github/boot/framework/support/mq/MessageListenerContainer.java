package com.github.boot.framework.support.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.ParameterizedType;
import java.util.*;

/**
 * 消息监听器容器
 * @author cjh
 * @version 1.0
 */
public abstract class MessageListenerContainer implements ApplicationContextAware {

	private static final Logger LOGGER = LoggerFactory.getLogger(MessageListenerContainer.class);

	protected Map<MessageTopic,List<MessageListener<AbstractMessage>>> listenerMap =  new HashMap<>();
	
	/**
	 * 关闭监听容器
	 */
	public abstract void shutdown();

	/**
	 * 根据监听频道获取监听器
	 * @return
	 */
	public List<MessageListener<AbstractMessage>> getListeners(MessageTopic topic){
		return this.listenerMap.get(topic);
	}

	/**
	 * 获取所有的监听监听频道
	 * @return
	 */
	public Set<MessageTopic> getAllTopics(){
		return this.listenerMap.keySet();
	}

	/**
	 * 设置ApplicationContext
	 * @param applicationContext
	 * @throws BeansException
	 */
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked"})
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		Map<String, MessageListener> map = applicationContext.getBeansOfType(MessageListener.class);
		for (MessageListener listener : map.values()) {
			ParameterizedType type = (ParameterizedType) listener.getClass().getGenericInterfaces()[0];
			Class<AbstractMessage> clazz = (Class<AbstractMessage>) type.getActualTypeArguments()[0];
			MessageTopic channel = getMessageTopic(clazz);
			List<MessageListener<AbstractMessage>> listeners = listenerMap.get(channel);
			if(listeners == null){
				listeners = new ArrayList<>();
			}
			listeners.add(listener);
			listenerMap.put(channel, listeners);
			LOGGER.info("MQ消息主题：{}", channel.value());
		}
	}

	/**
	 * 根据消息体类型获取消息频道
	 * @param clazz 消息体类型
	 * @return
	 */
	private MessageTopic getMessageTopic(Class<? extends AbstractMessage> clazz){
		MessageTopic channel = clazz.getAnnotation(MessageTopic.class);
		if(channel == null){
			throw new RuntimeException("消息实体没有设置消息发送主题: " + clazz.getName());
		}
		return channel;
	}



}
