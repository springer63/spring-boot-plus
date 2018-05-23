package com.github.boot.framework.config;

import com.github.boot.framework.support.mq.AbstractMessage;
import com.github.boot.framework.support.mq.onsmq.OnsMessageListenerContainer;
import com.github.boot.framework.support.mq.onsmq.OnsMessageProducer;
import com.github.boot.framework.support.serializer.JacksonSerializer;
import com.github.boot.framework.support.serializer.Serializer;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;

/**
 * 队列消息配置
 *
 * @author cjh
 * @date 2017/3/12
 */
@Configurable
public class OnsMqConfigure {

    @Value("${aliyun.ons.secretKey}")
    private String secretKey;

    @Value("${aliyun.ons.accessKey}")
    private String accessKey;

    @Value("${aliyun.ons.producerId}")
    private String producerId;

    @Value("${aliyun.ons.consumerId}")
    private String consumerId;

    @Value("${aliyun.ons.consumerThreadNums:10}")
    private int consumerThreadNums;

    @Bean
    @ConditionalOnExpression("'${message.queue.side}'!= 'consumer'")
    public OnsMessageProducer messageProducer(){
        OnsMessageProducer producer = new OnsMessageProducer();
        producer.setSecretKey(this.secretKey);
        producer.setAccessKey(this.accessKey);
        producer.setProducerId(this.producerId);
        producer.setSerializer(serializer());
        return producer;
    }

    @Bean
    @ConditionalOnExpression("'${message.queue.side}'!= 'producer'")
    public OnsMessageListenerContainer messageListenerContainer(){
        OnsMessageListenerContainer listenerContainer = new OnsMessageListenerContainer();
        listenerContainer.setSecretKey(this.secretKey);
        listenerContainer.setAccessKey(this.accessKey);
        listenerContainer.setConsumerId(this.consumerId);
        listenerContainer.setConsumeThreadNums(this.consumerThreadNums);
        listenerContainer.setSerializer(serializer());
        return listenerContainer;
    }

    @Bean
    public Serializer<AbstractMessage> serializer(){
        return new JacksonSerializer<>();
    }
}
