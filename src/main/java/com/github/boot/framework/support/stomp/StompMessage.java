package com.github.boot.framework.support.stomp;

import com.github.boot.framework.support.mq.AbstractMessage;
import com.github.boot.framework.support.mq.MessageModel;
import com.github.boot.framework.support.mq.MessageTopic;

/**
 * STOMP消息实体
 * @author chenjianhui
 */
@MessageTopic(value = "stomp_message_topic", model = MessageModel.BROADCASTING)
public class StompMessage extends AbstractMessage {

    private static final long serialVersionUID = -8701368073164790685L;

    public StompMessage() {

    }

    public StompMessage(String topic, String body) {
        this.topic = topic;
        this.body = body;
    }

    public StompMessage(String userId, String topic, String body) {
        this.userId = userId;
        this.topic = topic;
        this.body = body;
    }

    /**
     * 接收消息用户ID(针对单个用户发送)
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

