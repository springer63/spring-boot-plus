package com.github.boot.framework.config;

import com.github.boot.framework.util.ValidUtils;
import org.redisson.api.RedissonClient;
import org.redisson.spring.session.RedissonSessionRepository;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.util.StringUtils;

/**
 * Session 共享配置
 * Created by cjh on 2017/3/12.
 */
@Configurable
@EnableSpringHttpSession
public class SessionConfigure {

    @Value("${session.key.prefix:}")
    private String keyPrefix;

    @Value("${session.cookie.domain:}")
    private String domainName;

    /**
     * Session有效期，以秒为单位， 默认30分钟
     */
    @Value("${session.max.interval:1800}")
    private Integer maxInactiveInterval;

    @Bean
    public RedissonSessionRepository sessionRepository(
            RedissonClient redissonClient, ApplicationEventPublisher eventPublisher) {
        RedissonSessionRepository repository = new RedissonSessionRepository(redissonClient, eventPublisher);
        if (StringUtils.hasText(keyPrefix)) {
            repository.setKeyPrefix(keyPrefix);
        }
        repository.setDefaultMaxInactiveInterval(maxInactiveInterval);
        return repository;
    }

    @Bean
    public CookieSerializer cookieSerializer(){
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        if(ValidUtils.isValid(domainName)){
            cookieSerializer.setDomainName(domainName);
        }
        return cookieSerializer;
    }

}
