package com.github.boot.framework.support.mq.rocketmq;

import com.github.boot.framework.support.mq.AbstractMessage;
import com.github.boot.framework.support.mq.MessageProducer;
import com.github.boot.framework.support.serializer.JacksonSerializer;
import com.github.boot.framework.support.serializer.Serializer;
import com.github.boot.framework.util.JsonUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Rocket MQ Producer
 *
 * @author cjh
 * @date 2017/3/15
 */
public class RocketMessageProducer implements InitializingBean, DisposableBean, MessageProducer {

    private static final Logger logger = LoggerFactory.getLogger(RocketMessageProducer.class);

    private String namesrv;

    private Serializer<AbstractMessage> serializer;

    private DefaultMQProducer producer;

    @Override
    public void send(AbstractMessage message) {
        try {
            producer.send(createRocketMessage(message));
            logger.info("发送消息成功：[" + message.topic() + "],content:{}", JsonUtils.toJson(message));
        } catch (Exception e) {
            logger.info(ExceptionUtils.getFullStackTrace(e));
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendAsync(final AbstractMessage message) {
        try {
            producer.send(createRocketMessage(message), new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    logger.info("发送消息成功：[" + message.topic() + "],content:{}", JsonUtils.toJson(message));
                }

                @Override
                public void onException(Throwable paramThrowable) {
                    logger.error("发送消息出错：[" + message.topic() + "]" + ExceptionUtils.getFullStackTrace(paramThrowable));
                }
            });
        } catch (Exception e) {
            logger.error("发送消息出错：[" + message.topic() + "]" + ExceptionUtils.getFullStackTrace(e));
        }
    }


    @Override
    public void destroy() {
        producer.shutdown();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (serializer == null) {
            serializer = new JacksonSerializer<>();
        }
        this.producer = new DefaultMQProducer("rocket_prod_group");
        producer.setNamesrvAddr(namesrv);
        producer.setInstanceName("RocketMqProducer");
        producer.setRetryTimesWhenSendAsyncFailed(16);
        producer.setSendMsgTimeout(6000);
        producer.start();
        logger.info("RocketMQ Producer Started ......");
    }

    /**
     * 转化为Rocket消息
     *
     * @param message
     */
    private org.apache.rocketmq.common.message.Message createRocketMessage(AbstractMessage message) {
        return new Message(message.topic(), message.tag(), message.getKey(), serializer.serialize(message));
    }

    public void setNamesrv(String namesrv) {
        this.namesrv = namesrv;
    }

    public Serializer<AbstractMessage> getSerializer() {
        return serializer;
    }

    public void setSerializer(Serializer<AbstractMessage> serializer) {
        this.serializer = serializer;
    }
}
