package com.github.boot.framework.web.listener;

import com.github.boot.framework.config.XdiamondConfigure.ConfigChangeEvent;
import org.slf4j.Logger;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.boot.logging.logback.LogbackLoggingSystem;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Logger Level Config Change Listener
 *
 * @author cjh
 * @date 2017/6/19
 */
@Component
public class LogLevelChangeListener implements ApplicationListener<ConfigChangeEvent> {

    private final static String LOGGING_LEVEL_ROOT = "logging.level.root";

    @Override
    public void onApplicationEvent(ConfigChangeEvent event) {
        if(LOGGING_LEVEL_ROOT.equals(event.getConfigKey())){
            System.setProperty(LoggingSystem.SYSTEM_PROPERTY, LogbackLoggingSystem.class.getName());
            LoggingSystem loggingSystem = LoggingSystem.get(this.getClass().getClassLoader());
            loggingSystem.setLogLevel(Logger.ROOT_LOGGER_NAME, LogLevel.valueOf(event.getConfigValue().toUpperCase()));
        }
    }
}
