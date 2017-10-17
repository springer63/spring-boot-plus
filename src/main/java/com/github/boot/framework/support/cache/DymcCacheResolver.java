package com.github.boot.framework.support.cache;

import com.github.boot.framework.util.ValidUtils;
import com.github.boot.framework.support.spring.ApplicationContextUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 根据SPEL表达式解析CacheName
 * Cache 动态解析器
 * Created by cjh on 2017/4/26.
 */
public class DymcCacheResolver implements CacheResolver {

    private CacheManager cacheManager;

    private ExpressionParser parser = new SpelExpressionParser();

    @Override
    public Collection<? extends Cache> resolveCaches(CacheOperationInvocationContext<?> context) {
        if(cacheManager == null){
            cacheManager = ApplicationContextUtils.getContext().getBean(CacheManager.class);
        }
        List<Cache> caches = new ArrayList<>(context.getOperation().getCacheNames().size());
        if(!ValidUtils.isValid(context.getArgs())){
            for (String cacheName : context.getOperation().getCacheNames()) {
                caches.add(cacheManager.getCache(cacheName));
            }
            return caches;
        }
        EvaluationContext evaluationContext = new StandardEvaluationContext();
        for (int i = 0; i < context.getArgs().length; i++) {
            evaluationContext.setVariable("p" + i, context.getArgs()[i]);
        }
        for(String cacheName : context.getOperation().getCacheNames()) {
            cacheName = parser.parseExpression(cacheName).getValue(evaluationContext, String.class);
            caches.add(cacheManager.getCache(cacheName));
        }
        return caches;
    }

}
