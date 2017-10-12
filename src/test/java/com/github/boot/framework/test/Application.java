package com.github.boot.framework.test;

import com.github.boot.framework.config.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by cjh on 2017/10/11.
 */
@Import({
        UploadConfigure.class,
        DataSourceConfigure.class,
        ServletConfigure.class,
        RedisConfigure.class,
        SessionConfigure.class,
        CacheConfigure.class,
        MqConfigure.class
})
@ComponentScan("com.github.boot.framework.test")
@SpringBootApplication
@PropertySource("classpath:application.properties")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
