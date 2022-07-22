package com.ptn.internal.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public class SpringBeanUtil {
    private static Logger A = LoggerFactory.getLogger(SpringBeanUtil.class);

    public SpringBeanUtil() {
    }
    public static <T> T getBean(Class<T> var0) {
        return getApplicationContext().getBean(var0);
    }

    public static Object lookup(String var0) {
        return getApplicationContext().getBean(var0);
    }

    public static <T> T lookup(String var0, Class<T> var1) {
        return getApplicationContext().getBean(var0, var1);
    }

    public static ApplicationContext getApplicationContext() {
        return ApplicationContextFactory.getApplicationContext();
    }
}
