package com.github.boot.framework.config;

import com.github.boot.framework.support.stomp.StompMessageListener;
import com.github.boot.framework.util.ConstUtils;
import com.github.boot.framework.util.ServletUtils;
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
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
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
	public StompMessageListener stompMessageListener(){
		return new StompMessageListener();
	}

	@Override
	protected void configureStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws")
				.addInterceptors(new SessionAuthHandshakeInterceptor())
				.setHandshakeHandler(new DefaultHandshakeHandler() {
					@Override
					protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
						ServletServerHttpRequest serverRequest = (ServletServerHttpRequest) request;
						HttpSession session = serverRequest.getServletRequest().getSession(false);
						if(session == null){
							return request.getPrincipal();
						}
						Object userId = session.getAttribute(ConstUtils.SESSION_USER_ID);
						String clientIp = ServletUtils.getClientIp(serverRequest.getServletRequest());
						return new DefaultPrincipal(userId, clientIp);
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
		registry.setMessageSizeLimit(512 * 1024).setSendBufferSizeLimit(512 * 1024).setSendTimeLimit(20000);
	}

	/**
	 * 输入通道参数设置
	 */
	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		//设置消息输入通道的线程池线程数
		registration.taskExecutor().corePoolSize(10).maxPoolSize(200).keepAliveSeconds(60);
		registration.interceptors(new ChannelInterceptorAdapter() {
			@Override
			public Message<?> preSend(Message<?> message, MessageChannel channel) {
				StompHeaderAccessor sha = StompHeaderAccessor.wrap(message);
				StompCommand command = sha.getCommand();
				if(command == StompCommand.DISCONNECT){
					Principal user = sha.getUser();
					if(user != null){
						logger.info("用户{}已和服务器断开连接", user.getName());
					}
				}
				return super.preSend(message, channel);
			}
		});
	}

	/**
	 * 输出通道参数设置
	 */
	@Override
	public void configureClientOutboundChannel(ChannelRegistration registration) {
		registration.taskExecutor().corePoolSize(10).maxPoolSize(200);
		registration.interceptors(new ChannelInterceptorAdapter() {
			@Override
			public Message<?> preSend(Message<?> message, MessageChannel channel) {
				StompHeaderAccessor sha = StompHeaderAccessor.wrap(message);
				sha.setHeartbeat(1000, 1000);
				sha.setHeader("ts", System.currentTimeMillis());
				return message;
			}
		});
	}

	public static class SessionAuthHandshakeInterceptor implements HandshakeInterceptor {

		@Override
		public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
				   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
			return true;
		}

		@Override
		public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
				   WebSocketHandler wsHandler, Exception exception) {
			if(exception != null){
				logger.info(exception.getMessage());
				return;
			}
			ServletServerHttpRequest serverRequest = (ServletServerHttpRequest) request;
			HttpSession session = serverRequest.getServletRequest().getSession(false);
			Object userId = null;
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
	public static class DefaultPrincipal implements Principal{

		/**
		 * 用户ID
		 */
		private Object userId;

		/**
		 * 客户端IP
		 */
		private String clientIp;

		public DefaultPrincipal(Object userId, String clientIp) {
			this.userId = userId;
			this.clientIp = clientIp;
		}

		@Override
		public String getName() {
			return String.valueOf(userId);
		}

		/**
		 * 获取客户端IP
		 * @return
		 */
		public String getClientIp() {
			return clientIp;
		}

	}

}
