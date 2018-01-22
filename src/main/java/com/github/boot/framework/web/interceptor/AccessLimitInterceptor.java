package com.github.boot.framework.web.interceptor;

import com.github.boot.framework.util.ConstUtils;
import com.github.boot.framework.util.ServletUtils;
import com.github.boot.framework.web.annotation.ApiAccessLimit;
import com.github.boot.framework.web.annotation.ApiAccessLimits;
import com.github.boot.framework.web.result.Result;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 接口访问频率控制拦截器
 * @author ChenJianhui
 * @date 2017/6/19
 */
public class AccessLimitInterceptor extends HandlerInterceptorAdapter{

	private static final String LOCKED_TARGET_PREFIX = "api:lock:target:";

	private static final String ACCESS_COUNT_PREFIX = "api:access:count:";

	private final RedissonClient redisClient;

	public AccessLimitInterceptor(RedissonClient client){
		this.redisClient = client;
	}

	/**
	 * 接口访问频率控制拦截
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if(!(handler instanceof  HandlerMethod)){
			return true;
		}
		HandlerMethod method = (HandlerMethod) handler;
		ApiAccessLimits annotation = method.getMethodAnnotation(ApiAccessLimits.class);
		if(annotation == null){
			return true;
		}
		Object userId = request.getSession().getAttribute(ConstUtils.SESSION_USER_ID);
		String ip = ServletUtils.getClientIp(request);
		String lockedKey = null;
		String accessKey = null;
		ApiAccessLimit[] accessLimits = annotation.value();
		for (ApiAccessLimit l : accessLimits){
			if(l.type() == 1){
				if(userId == null){
					continue;
				}
				lockedKey = LOCKED_TARGET_PREFIX + request.getRequestURI() + userId;
				accessKey = ACCESS_COUNT_PREFIX + request.getRequestURI() + userId;
			}else{
				if(ip == null || "".equals(ip)){
					continue;
				}
				lockedKey = LOCKED_TARGET_PREFIX + request.getRequestURI() + ip;
				accessKey = ACCESS_COUNT_PREFIX + request.getRequestURI() + ip;
			}
			RBucket<Object> lockBucket = redisClient.getBucket(lockedKey);
			if(lockBucket.isExists()){
				request.getRequestDispatcher("/error/" + Result.SYSTEM_BUSY).forward(request, response);
				return false;
			}
			RAtomicLong atomic = redisClient.getAtomicLong(accessKey);
			long count = atomic.incrementAndGet();
			if(count > l.frequency()){
				lockBucket.setAsync(1, l.lockTime(), l.timeUnit());
				request.getRequestDispatcher("/error/" + Result.SYSTEM_BUSY).forward(request, response);
				return false;
			}
			if(count == 1){
				atomic.expire(l.interval(), l.timeUnit());
			}
		}
		return true;
	}

}
