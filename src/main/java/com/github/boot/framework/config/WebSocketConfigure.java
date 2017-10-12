package com.github.boot.framework.config;

import com.github.boot.framework.util.ConstUtils;
import com.github.boot.framework.util.ServletUtils;
import com.github.boot.framework.util.stomp.StompMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.session.ExpiringSession;
import org.springframework.session.web.socket.config.annotation.AbstractSessionWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Map;

/**
 * @Description WebSocket Configure
 * @author cjh
 * @version 1.0
 * @date：2017年3月12日 下午2:42:04
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfigure extends AbstractSessionWebSocketMessageBrokerConfigurer<ExpiringSession> {

	private static final Logger logger = LoggerFactory.getLogger(WebSocketConfigure.class);

	@Bean
	public StompMessageSender stompMessageSender(){
		return new StompMessageSender();
	}

	@Override
	protected void configureStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws")
				.addInterceptors(new SessionAuthHandshakeInterceptor())
				.setHandshakeHandler(new DefaultHandshakeHandler() {
					@Override
					protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
						return new MyPrincipal(attributes.get("user"));
					}
				})
				.setAllowedOrigins("*");
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		//表示在topic和user这两个域上可以向客户端发消息
		registry.enableSimpleBroker("/queue/", "/topic/");
		//表示客户端向服务端发送时的主题上面需要加"/app"作为前缀
		registry.setApplicationDestinationPrefixes("/app");
		//表示给指定用户发送（一对一）的主题前缀
		registry.setUserDestinationPrefix("/user/");
	}
	
	/**
	 * 消息传输参数配置
	 */
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
		registry.setMessageSizeLimit(512 * 1024) // 设置消息字节数大小
				.setSendBufferSizeLimit(512 * 1024)// 设置消息缓存大小
				.setSendTimeLimit(20000); // 设置消息发送时间限制毫秒

    }
    
    /**
     * 输入通道参数设置
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.taskExecutor().corePoolSize(4) //设置消息输入通道的线程池线程数
        .maxPoolSize(10)//最大线程数
        .keepAliveSeconds(60);//线程活动时间
		registration.setInterceptors(new ChannelInterceptorAdapter() {
			@Override
			public Message<?> preSend(Message<?> message, MessageChannel channel) {
				/*MessageHeaders headers = message.getHeaders();
				Object topic = headers.get(StompHeaderAccessor.DESTINATION_HEADER);
				if(topic != null && !topic.toString().startsWith("/app")){
					message = null;
				}*/
				return super.preSend(message, channel);
			}
		});
    }
    
    /**
     * 输出通道参数设置
     */
    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.taskExecutor().corePoolSize(4).maxPoolSize(20);
        registration.setInterceptors(new ChannelInterceptorAdapter() {
			@Override
			public Message<?> preSend(Message<?> message, MessageChannel channel) {
				logger.info("preSend ....");
				return super.preSend(message, channel);
			}

			@Override
			public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
				logger.info("postSend ....");
				super.postSend(message, channel, sent);
			}

			@Override
			public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
				logger.info("afterSendCompletion ...");
				super.afterSendCompletion(message, channel, sent, ex);
			}

			@Override
			public boolean preReceive(MessageChannel channel) {
				logger.info("preReceive ...");
				return super.preReceive(channel);
			}

			@Override
			public Message<?> postReceive(Message<?> message, MessageChannel channel) {
				logger.error("postReceive ...");
				return super.postReceive(message, channel);
			}

			@Override
			public void afterReceiveCompletion(Message<?> message, MessageChannel channel, Exception ex) {
				logger.error("afterReceiveCompletion ...");
				super.afterReceiveCompletion(message, channel, ex);
			}
		});
    }

	static class SessionAuthHandshakeInterceptor implements HandshakeInterceptor {

		@Override
		public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
			return true;
		}

		@Override
		public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
			if(exception != null){
				logger.info(exception.getMessage());
				return;
			}
			ServletServerHttpRequest serverRequest = (ServletServerHttpRequest) request;
			HttpSession session = serverRequest.getServletRequest().getSession(false);
			Object userId = 0;
			if(session != null){
				userId = session.getAttribute(ConstUtils.SESSION_USER_ID);
			}
			userId = userId == null ? ServletUtils.getClientIp(serverRequest.getServletRequest()): userId;
			logger.info("用户{}与服务器建立WebSocket链接", userId);
		}
	}

	/**
	 * 用户主要信息
	 */
	static class MyPrincipal implements Principal{

		private Object userId = 0;

		public MyPrincipal(Object userId) {
			this.userId = userId;
		}

		@Override
		public String getName() {
			return String.valueOf(userId);
		}

	}


}
