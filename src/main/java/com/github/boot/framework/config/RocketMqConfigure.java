package com.github.boot.framework.config;

import com.github.boot.framework.support.mq.AbstractMessage;
import com.github.boot.framework.support.mq.rocketmq.RocketMessageListenerContainer;
import com.github.boot.framework.support.mq.rocketmq.RocketMessageProducer;
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
public class RocketMqConfigure {

    @Value("${rocket.mq.namesrv}")
    private String namesrv;

    @Value("${rocket.mq.consumer.group-name:${spring.application.name}}")
    private String groupName;

	@Bean
    public Serializer<AbstractMessage>  serializer(){
        return new JacksonSerializer<>();
    }

    @Bean
    @ConditionalOnExpression("'${message.queue.side}'!= 'consumer'")
    public RocketMessageProducer messageProducer(){
        RocketMessageProducer producer = new RocketMessageProducer();
        producer.setNamesrv(this.namesrv);
        producer.setSerializer(serializer());
        return producer;
    }

    @Bean
    @ConditionalOnExpression("'${message.queue.side}'!= 'producer'")
    public RocketMessageListenerContainer messageListenerContainer(){
        RocketMessageListenerContainer listenerContainer = new RocketMessageListenerContainer();
        listenerContainer.setNamesrv(namesrv);
        listenerContainer.setGroupName(groupName);
        listenerContainer.setSerializer(serializer());
        return listenerContainer;
    }

}
