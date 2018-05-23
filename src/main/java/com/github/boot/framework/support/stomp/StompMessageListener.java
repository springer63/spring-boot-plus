package com.github.boot.framework.support.stomp;

import com.github.boot.framework.support.mq.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;

/**
 * StompMessageListener
 *
 * @author chenjianhui
 * @create 2018/05/23
 **/
public class StompMessageListener implements MessageListener<StompMessage> {

    private static final Logger logger = LoggerFactory.getLogger(StompMessageSender.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

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
}
