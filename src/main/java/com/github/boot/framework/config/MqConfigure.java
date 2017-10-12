package com.github.boot.framework.config;

import com.github.boot.framework.support.mq.Message;
import com.github.boot.framework.support.mq.rocketmq.RocketMessageListenerContainer;
import com.github.boot.framework.support.mq.rocketmq.RocketMessageProducer;
import com.github.boot.framework.support.serializer.JacksonSerializer;
import com.github.boot.framework.support.serializer.Serializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

/**
 * 队列消息配置
 * Created by cjh on 2017/3/12.
 */
@Configurable
public class MqConfigure {

    @Value("${mq.namesrv}")
    private String namesrv;

    @Value("${mq.consumer.group-name:mqConsumer}")
    private String groupName;

	@Bean
	@SuppressWarnings({ "unchecked", "rawtypes" })
    public Serializer<Message>  serializer(){
        return new JacksonSerializer();
    }

    @Bean
    @Autowired
    public RocketMessageProducer messageProducer(Serializer<Message> serializer){
        RocketMessageProducer producer = new RocketMessageProducer();
        producer.setNamesrv(this.namesrv);
        producer.setSerializer(serializer);
        return producer;
    }

    @Bean
    @Autowired
    public RocketMessageListenerContainer messageListenerContainer(Serializer<Message> serializer){
        RocketMessageListenerContainer listenerContainer = new RocketMessageListenerContainer();
        listenerContainer.setNamesrv(namesrv);
        listenerContainer.setGroupName(groupName);
        listenerContainer.setSerializer(serializer);
        return listenerContainer;
    }

   /* @Value("${aliyun.ons.secretKey}")
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
    @Autowired
    public OnsMessageProducer messageProducer(Serializer<Message> serializer){
        OnsMessageProducer producer = new OnsMessageProducer();
        producer.setSecretKey(this.secretKey);
        producer.setAccessKey(this.accessKey);
        producer.setProducerId(this.producerId);
        producer.setSerializer(serializer);
        return producer;
    }

    @Bean
    @Autowired
    public OnsMessageListenerContainer messageListenerContainer(Serializer<Message> serializer){
        OnsMessageListenerContainer listenerContainer = new OnsMessageListenerContainer();
        listenerContainer.setSecretKey(this.secretKey);
        listenerContainer.setAccessKey(this.accessKey);
        listenerContainer.setConsumerId(this.consumerId);
        listenerContainer.setConsumeThreadNums(this.consumerThreadNums);
        listenerContainer.setSerializer(serializer);
        return listenerContainer;
    }*/
}
