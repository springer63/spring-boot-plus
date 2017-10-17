package com.github.boot.framework.support.mq.onsmq;

import com.aliyun.openservices.ons.api.*;
import com.github.boot.framework.support.mq.AbstractMessage;
import com.github.boot.framework.support.mq.MessageListener;
import com.github.boot.framework.support.mq.MessageListenerContainer;
import com.github.boot.framework.support.mq.MessageTopic;
import com.github.boot.framework.support.serializer.Serializer;
import com.github.boot.framework.util.JsonUtils;
import com.github.boot.framework.util.ValidUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Properties;
import java.util.Set;

/**
 * ONS消息监听容器
 * @author cjh
 * @version 1.0
 */
public class OnsMessageListenerContainer extends MessageListenerContainer implements InitializingBean, DisposableBean {

	private final static Logger logger = LoggerFactory.getLogger(OnsMessageListenerContainer.class);

	private String consumerId;
	
	private String accessKey;

	private String secretKey;

	private int consumeThreadNums = 10;

	private Consumer consumer;

	@Autowired(required=false)
	private OnsMessageProducer producer;//用来发送延迟消息的生产者

	private Serializer<AbstractMessage> serializer;

	@Override
	public void destroy() throws Exception {
		this.shutdown();
	}
	
	public void start() {
		if (!consumer.isStarted()) {
			this.consumer.start();
		}
	}

	@Override
	public void shutdown() {
		if (!consumer.isClosed()) {
			consumer.shutdown();
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Properties properties = new Properties();
		properties.put("ConsumerId", this.consumerId);
		properties.put("AccessKey", this.accessKey);
		properties.put("SecretKey", this.secretKey);
		properties.put("ConsumeThreadNums", this.consumeThreadNums);
		this.consumer = ONSFactory.createConsumer(properties);
		Set<MessageTopic> allTopics = getAllTopics();
		if(!ValidUtils.isValid(allTopics)){
			return;
		}
		com.aliyun.openservices.ons.api.MessageListener listener = new AliMessageListener();
		for (MessageTopic topic : getAllTopics()) {
			this.consumer.subscribe(topic.value(), "*", listener);
		}
		this.consumer.start();
	}

	/**
	 * ONS消息监听实现类
	 */
	private class AliMessageListener implements com.aliyun.openservices.ons.api.MessageListener {
		@Override
		public Action consume(Message onsMsg, ConsumeContext context) {
			try {
				AbstractMessage message = serializer.deserialize(onsMsg.getBody());
				logger.info("接收消息成功TOPIC:{}, ID:{}, CONTENT:{}", message.topic(), onsMsg.getMsgID(), JsonUtils.toJson(message));
				if(message instanceof ScheduledMessage && producer != null){
					ScheduledMessage scheduledMessage = (ScheduledMessage) message;
					if(scheduledMessage.getDeliverTime() - System.currentTimeMillis() > OnsMessageProducer.MSG_MAX_STORE_TIME){
						producer.sendAsync(message);
					}
				}
				message.setKey(onsMsg.getKey());
				MessageTopic topic = message.getClass().getAnnotation(MessageTopic.class);
				if(getListeners(topic) == null){
					return Action.ReconsumeLater;
				}
				for (MessageListener<AbstractMessage> listener : getListeners(topic)) {
					listener.onMessage(message);
				}
				return Action.CommitMessage;
			} catch (Exception e) {
				if(e instanceof ClassCastException){
					logger.error("消息类型不匹配：" + e.getMessage());
					return Action.CommitMessage;
				}else{
					logger.error("消息处理异常：", e);
					return Action.ReconsumeLater;
				}
			}
		}
	}
	

	public void setConsumerId(String consumerId) {
		this.consumerId = consumerId;
	}

	public String getAccessKey() {
		return accessKey;
	}
	
	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}
	
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}
	
	public Serializer<AbstractMessage> getSerializer() {
		return serializer;
	}

	public void setSerializer(Serializer<AbstractMessage> serializer) {
		this.serializer = serializer;
	}
	
	public void setConsumeThreadNums(int consumeThreadNums) {
		this.consumeThreadNums = consumeThreadNums;
	}
	
}
