package com.github.boot.framework.support.mq.onsmq;

import com.aliyun.openservices.ons.api.*;
import com.aliyun.openservices.ons.api.Message;
import com.github.boot.framework.support.mq.AbstractMessage;
import com.github.boot.framework.support.mq.MessageProducer;
import com.github.boot.framework.support.serializer.Serializer;
import com.github.boot.framework.util.JsonUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.Properties;

/**
 * 阿里云消息生产者
 * @author cjh
 * @version 1.0
 */
public class OnsMessageProducer implements InitializingBean , DisposableBean, MessageProducer {

	/**
	 * 普通消息如果消费不成功， 最多保存三天
	 * 定时/延时消息 msg.setStartDeliverTime 的参数可设置40天内的任何时刻（单位毫秒），
	 * 超过40天消息发送将失败。
	 * @see //help.aliyun.com/document_detail/43349.html?spm=5176.doc29532.6.573.1MLbWC
	 */
	public final static long MSG_MAX_STORE_TIME = 39L * 24L * 60L * 60L * 1000L;
	
	private String producerId;
	
	private String accessKey;
	
	private String secretKey;
	
	private Producer producer;
	
	private Serializer<AbstractMessage> serializer;
	
	private static final Logger logger = LoggerFactory.getLogger(OnsMessageProducer.class);
    
    /**
	 * 同步发送消息，只要不抛异常就表示成功
	 * @param message 消息
	 */
    @Override
    public void send(AbstractMessage message){
    	Message onsMsg = createONSMessage(message);
    	producer.send(onsMsg);
		logger.info("发送消息成功TOPIC:{}, CONTENT:{}", message.topic(), JsonUtils.toJson(message));
    }
    
    /**
	 * 单向发送消息，Oneway形式，服务器不应答，无法保证消息是否成功到达服务器
	 * @param message 消息
	 */
    public void sendOneway(AbstractMessage message){
    	Message onsMsg = createONSMessage(message);
    	producer.sendOneway(onsMsg);
		logger.info("发送消息成功TOPIC:{}, CONTENT:{}", message.topic(), JsonUtils.toJson(message));
    }

    /**
   	 * 发送异步消息，异步Callback形式
   	 * @param message 消息
   	 */
    @Override
    public void sendAsync(final AbstractMessage message){
    	Message onsMsg = createONSMessage(message);
		producer.sendAsync(onsMsg, new SendCallback() {
			@Override
			public void onSuccess(SendResult sendResult) {
				logger.info("发送消息成功TOPIC:{}, CONTENT:{}", message.topic(), JsonUtils.toJson(message));
			}
			@Override
			public void onException(OnExceptionContext context) {
				logger.error("发送消息失败TOPIC:{}, CONTENT:{}，EXCEPTION:{}", message.topic(), JsonUtils.toJson(message), context.getException().getMessage());
				logger.error(ExceptionUtils.getFullStackTrace(context.getException()));
			}
		});
    }

    @Override
	public void destroy() throws Exception {
		if (this.producer != null) {
			this.producer.shutdown();
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
        Properties properties = new Properties();
        properties.put("ProducerId", this.getProducerId());
        properties.put("AccessKey", this.accessKey);
        properties.put("SecretKey", this.secretKey);
		this.producer = ONSFactory.createProducer(properties);
        this.producer.start();
	}
	
	/**
	 * 转化为ONS消息
	 * @param message
	 */
	private Message createONSMessage(AbstractMessage message){
		Message onsMsg = new Message(message.topic(), "*", message.getKey(), serializer.serialize(message));
		if(message instanceof  ScheduledMessage){
			Long deliverTime = ((ScheduledMessage)message).getDeliverTime();
			long currMillis = System.currentTimeMillis();
			if(deliverTime - currMillis > MSG_MAX_STORE_TIME){
				deliverTime = currMillis + MSG_MAX_STORE_TIME;
			}
			onsMsg.setStartDeliverTime(deliverTime);
		}
		return onsMsg;
	}
	
	public String getProducerId() {
		return producerId;
	}

	public void setProducerId(String producerId) {
		this.producerId = producerId;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getSecretKey() {
		return secretKey;
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

	public void start() {
		if (!producer.isStarted()) {
			this.producer.start();
		}
	} 
	
	public void shutdown() {
		if (!producer.isClosed()) {
			this.producer.shutdown();
		}
	}
	
	
	
}
