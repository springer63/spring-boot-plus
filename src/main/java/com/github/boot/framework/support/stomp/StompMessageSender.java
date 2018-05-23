package com.github.boot.framework.support.stomp;

import com.github.boot.framework.support.mq.MessageProducer;

/**
 * 基于 STOMP 协议 WebSocket 消息发送工具类
 *
 * @author cjh
 * @date 2017/6/24
 */
public class StompMessageSender {

    private MessageProducer messageProducer;

    public StompMessageSender(MessageProducer messageProducer) {
        this.messageProducer = messageProducer;
    }

    /**
     * 发送消息给指定的用户
     * @param userId 用户ID
     * @param topic 订阅主题
     * @param body 消息体
     */
    public void sendToUser(String userId, String topic, String body){
        StompMessage message = new StompMessage();
        message.setUserId(userId);
        message.setTopic(topic);
        message.setBody(body);
        messageProducer.sendAsync(message);
    }

    /**
     * 发送消息给所有用户
     * @param topic 订阅主题
     * @param body 消息体
     */
    public void sendToAll(String topic, String body){
        StompMessage message = new StompMessage();
        message.setTopic(topic);
        message.setBody(body);
        messageProducer.sendAsync(message);
    }



}

