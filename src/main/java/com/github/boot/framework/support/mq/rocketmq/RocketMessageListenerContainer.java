package com.github.boot.framework.support.mq.rocketmq;

import com.github.boot.framework.support.mq.AbstractMessage;
import com.github.boot.framework.support.mq.MessageListener;
import com.github.boot.framework.support.mq.AbstractMessageListenerContainer;
import com.github.boot.framework.support.mq.MessageTopic;
import com.github.boot.framework.support.serializer.JacksonSerializer;
import com.github.boot.framework.support.serializer.Serializer;
import com.github.boot.framework.util.JsonUtils;
import com.github.boot.framework.util.ValidUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;

/**
 * Rocket MQ Listener Container
 *
 * @author cjh
 * @date 2017/3/15
 */
public class RocketMessageListenerContainer extends AbstractMessageListenerContainer implements InitializingBean, DisposableBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(RocketMessageListenerContainer.class);

    private String namesrv;

    private String groupName;

    private DefaultMQPushConsumer clusterConsumer;

    private DefaultMQPushConsumer broadcastConsumer;

    private Serializer<AbstractMessage> serializer = new JacksonSerializer<>();

    @Override
    public void shutdown() {
        if(clusterConsumer != null){
            clusterConsumer.shutdown();
        }
        if(broadcastConsumer != null){
            broadcastConsumer.shutdown();
        }
    }

    @Override
    public void destroy(){
        this.shutdown();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if(!ValidUtils.isValid(getAllTopics())){
            return;
        }
        for(MessageTopic topic : getAllTopics()){
            if(topic.model() == com.github.boot.framework.support.mq.MessageModel.CLUSTERING){
                getClusterConsumer().subscribe(topic.value(), "*");
            }else{
                getBroadcastConsumer().subscribe(topic.value(), "*");
            }
        }
        if(this.broadcastConsumer != null){
            broadcastConsumer.registerMessageListener(new RocketMessageListener());
            broadcastConsumer.start();
            LOGGER.info("RocketMQBroadcastConsumer Started ......");
        }
        if(this.clusterConsumer != null){
            clusterConsumer.registerMessageListener(new RocketMessageListener());
            clusterConsumer.start();
            LOGGER.info("RocketMQClusterConsumer Started ......");
        }
    }

    /**
     * 获取广播消费模式的消费端
     * @return
     */
    private DefaultMQPushConsumer getBroadcastConsumer() {
        if(this.broadcastConsumer != null){
            return this.broadcastConsumer;
        }
        broadcastConsumer = new DefaultMQPushConsumer("TaskBroadcastConsumerGroup");
        broadcastConsumer.setNamesrvAddr(namesrv);
        broadcastConsumer.setInstanceName("RocketMQBroadcastConsumer");
        broadcastConsumer.setMessageModel(MessageModel.BROADCASTING);
        broadcastConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_TIMESTAMP);
        return this.broadcastConsumer;
    }

    /**
     * 获取集群消费模式的消费端
     * @return
     */
    private DefaultMQPushConsumer getClusterConsumer() {
        if(this.clusterConsumer != null){
            return this.clusterConsumer;
        }
        clusterConsumer = new DefaultMQPushConsumer("TaskClusterConsumerGroup");
        clusterConsumer.setNamesrvAddr(namesrv);
        clusterConsumer.setInstanceName("RocketMQClusterConsumer");
        clusterConsumer.setMessageModel(MessageModel.CLUSTERING);
        clusterConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_TIMESTAMP);
        return this.clusterConsumer;
    }

    /**
     * 自定义消息监听器
     */
    private class RocketMessageListener implements MessageListenerConcurrently{
        @Override
        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
            try{
                for (final MessageExt msg : msgs){
                    final AbstractMessage message = serializer.deserialize(msg.getBody());
                    LOGGER.info("接收消息成功TOPIC:{}, ID:{} CONTENT:{}", message.topic(), msg.getMsgId(), JsonUtils.toJson(message));
                    List<MessageListener<AbstractMessage>> listeners = getListeners(message.getClass().getAnnotation(MessageTopic.class));
                    for(final MessageListener<AbstractMessage> listener : listeners){
                        listener.onMessage(message);
                        LOGGER.info("消费消息成功TOPIC:{}, ID:{} CONTENT:{}", message.topic(), msg.getMsgId(), JsonUtils.toJson(message));
                    }
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }catch (Exception e){
                LOGGER.error(ExceptionUtils.getFullStackTrace(e));
                for (final MessageExt msg : msgs){
                    if(msg.getReconsumeTimes() == 5){
                        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                    }
                }
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        }
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    
    public String getGroupName(){
    	return this.groupName;
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
