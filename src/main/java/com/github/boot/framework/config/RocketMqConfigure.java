package com.github.boot.framework.config;

import com.github.boot.framework.support.mq.AbstractMessage;
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
 *
 * @author cjh
 * @date 2017/3/12
 */
@Configurable
public class RocketMqConfigure {

    @Value("${mq.namesrv}")
    private String namesrv;

    @Value("${mq.consumer.group-name:mqConsumer}")
    private String groupName;

	@Bean
	@SuppressWarnings({ "unchecked", "rawtypes" })
    public Serializer<AbstractMessage>  serializer(){
        return new JacksonSerializer();
    }

    @Bean
    @Autowired
    public RocketMessageProducer messageProducer(Serializer<AbstractMessage> serializer){
        RocketMessageProducer producer = new RocketMessageProducer();
        producer.setNamesrv(this.namesrv);
        producer.setSerializer(serializer);
        return producer;
    }

    @Bean
    @Autowired
    public RocketMessageListenerContainer messageListenerContainer(Serializer<AbstractMessage> serializer){
        RocketMessageListenerContainer listenerContainer = new RocketMessageListenerContainer();
        listenerContainer.setNamesrv(namesrv);
        listenerContainer.setGroupName(groupName);
        listenerContainer.setSerializer(serializer);
        return listenerContainer;
    }

}
