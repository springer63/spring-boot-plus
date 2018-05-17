package com.github.boot.framework.config;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.*;

/**
 * 任务调度执行线程池配置
 * Created by cjh on 2017/7/11.
 */
@Configurable
@EnableScheduling
public class ScheduleConfigure implements SchedulingConfigurer {

    @Value("${schedule.thread.pool.size:}")
    private Integer poolSize;

    private final static int DEFAULT_THREAD_POOL_SIZE = 10 * Runtime.getRuntime().availableProcessors();

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskExecutor());
    }

    @Bean(destroyMethod = "shutdown")
    public Executor taskExecutor() {
        if(poolSize == null){
            poolSize = DEFAULT_THREAD_POOL_SIZE;
        }
        return Executors.newScheduledThreadPool(poolSize);
    }
}
