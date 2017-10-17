package com.github.boot.framework.support.stomp;

import com.github.boot.framework.support.mq.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;

/**
 * 基于 STOMP 协议 WebSocket 消息发送工具类
 *
 * @author cjh
 * @date 2017/6/24
 */
public class StompMessageSender implements MessageListener<StompMessageSender.StompMessage> {

    private static final Logger logger = LoggerFactory.getLogger(StompMessageSender.class);

    @Autowired
    private MessageProducer messageProducer;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

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

    /**
     * 发送消息到客户端
     * @param message 消息
     */
    @Override
    public void onMessage(StompMessage message) {
        logger.info("send stomp message topic:" + message.getTopic());
        if(message.getUserId() == null){
            messagingTemplate.convertAndSend(message.getTopic(), message.getBody());
        }else{
            messagingTemplate.convertAndSendToUser(message.getUserId(), message.getTopic(), message.getBody());
        }
    }

    /**
     * STOMP消息实体
     */
    @MessageTopic(value = "stomp_message_topic", model = MessageModel.BROADCASTING)
    public static class StompMessage extends AbstractMessage {

		private static final long serialVersionUID = -8701368073164790685L;

		/**
         * 接收消息用户ID
         */
        private String userId;

        /**
         * 消息主题
         */
        private String topic;

        /**
         * 消息内容
         */
        private String body;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }
    }


}

