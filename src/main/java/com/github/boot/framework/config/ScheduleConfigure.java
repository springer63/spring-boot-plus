package com.github.boot.framework.config;

import com.github.boot.framework.support.spring.ApplicationContextUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 任务调度执行线程池配置
 *
 * @author cjh
 * @date 2017/7/11
 */
@EnableAsync
@Configurable
@EnableScheduling
public class ScheduleConfigure implements SchedulingConfigurer, AsyncConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleConfigure.class);

    private ThreadPoolTaskScheduler scheduler;

    @Value("${schedule.thread.pool.size:}")
    private Integer poolSize;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskExecutor());
    }

    @Bean(destroyMethod = "shutdown")
    public Executor taskExecutor() {
        if(scheduler != null){
            return scheduler;
        }
        scheduler = new ThreadPoolTaskScheduler();
        int processorSize = Runtime.getRuntime().availableProcessors();
        int corePoolSize = poolSize == null ? processorSize * 2 : poolSize;
        scheduler.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setPoolSize(corePoolSize);
        scheduler.initialize();
        scheduler.setErrorHandler(t -> {
            ApplicationEvent event = new TaskExceptionEvent(t);
            ApplicationContextUtils.getContext().publishEvent(event);
            logger.error(ExceptionUtils.getFullStackTrace(t));
        });
        return scheduler;
    }

    @Override
    public Executor getAsyncExecutor() {
        return taskExecutor();
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (throwable, method, objects) -> {
            ApplicationEvent event = new TaskExceptionEvent(throwable);
            ApplicationContextUtils.getContext().publishEvent(event);
            logger.error(ExceptionUtils.getFullStackTrace(throwable));
        };
    }

    /**
     * 任务执行异常事件
     */
    public static class TaskExceptionEvent extends ApplicationEvent{
        /**
         * Create a new ApplicationEvent.
         *
         * @param source the object on which the event initially occurred (never {@code null})
         */
        public TaskExceptionEvent(Object source) {
            super(source);
        }
    }
}
