package com.github.boot.framework.support.spring;

import org.springframework.context.ApplicationContext;

/**
 * Created by cjh on 2017/4/18.
 */
public class ApplicationContextUtils {

    private static ApplicationContext context;

    public static ApplicationContext getContext() {
        return context;
    }

    public static void setContext(ApplicationContext context) {
        ApplicationContextUtils.context = context;
    }
}
