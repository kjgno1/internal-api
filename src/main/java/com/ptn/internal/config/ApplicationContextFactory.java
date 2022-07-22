package com.ptn.internal.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component("applicationContextFactory")
public class ApplicationContextFactory implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    public ApplicationContextFactory() {
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}