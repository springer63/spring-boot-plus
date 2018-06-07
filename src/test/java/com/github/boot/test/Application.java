package com.github.boot.test;

import com.github.boot.framework.config.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.session.SessionAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * Created by cjh on 2017/10/11.
 */
@Import({
    UploadConfigure.class,
    DataSourceConfigure.class,
    ServletConfigure.class,
    RedisConfigure.class,
    CacheConfigure.class,
    ScheduleConfigure.class
})
@ComponentScan("com.github.boot.test")
@SpringBootApplication(exclude = SessionAutoConfiguration.class)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
