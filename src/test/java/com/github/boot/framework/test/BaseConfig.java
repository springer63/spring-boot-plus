package com.github.boot.framework.test;

import com.github.boot.framework.config.MqConfigure;
import com.github.boot.framework.config.DataSourceConfigure;
import com.github.boot.framework.config.RedisConfigure;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by dell on 2017/3/21.
 */
@Configuration
@Import({
    RedisConfigure.class,
	DataSourceConfigure.class,
	MqConfigure.class
})
@ComponentScan("com.github.hbpu.framework.test")
@PropertySource("classpath:application.properties")
public class BaseConfig {

}
