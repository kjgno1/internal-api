package com.ptn.internal;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.util.ClassUtils;

import java.net.URL;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
@ComponentScan(basePackages = {
        "com.ptn.internal.config",
        "com.ptn.internal.service",
        "com.ptn.internal.webservice",
        "com.ptn.internal.repository",
        "com.ptn.internal.security",
        "com.ptn.internal.security.jwt",
        "com.ptn.internal.security.service",
})
public class InternalApiApplication {

    public static void main(String[] args) {
        // for log4j Thread usage
        // Data from current threads will be passed to child threads.
        System.setProperty("isThreadContextMapInheritable", "true");

        // default for everyone
        System.setProperty("org.jboss.logging.provider", "slf4j");

        // to cater Tomcat logging
        System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");

        // to make all loggers asynchronous
        boolean lmaxDisruptorPresent = ClassUtils.isPresent("com.lmax.disruptor.dsl.Disruptor", InternalApiApplication.class.getClassLoader());
        if (lmaxDisruptorPresent)
        {
            System.setProperty("log4j2.contextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
            System.setProperty("log4j2.asyncLoggerRingBufferSize", (1024 * 1024) + ""); // default is 256 * 1024
        }

        String httpPort = System.getProperty("server.http.port");
        String httpsPort = System.getProperty("server.https.port");

        // backward compatible
        if (StringUtils.isEmpty(httpPort))
        {
            httpPort = System.getProperty("server.port");
        }

        if (StringUtils.isEmpty(httpPort) && StringUtils.isEmpty(httpsPort))
        {
            System.out.println("Please configure VM Arguments, -Dserver.http.port=8700 -Dserver.https.port=8743 -Xms512m -Xmx4096m");
            System.out.println("\r\nServer unable to start!");
            System.exit(0);
        }

        String profile = System.getProperty("spring.profiles.active");

        if (StringUtils.isEmpty(profile))
        {
            System.out.println("Please configure VM Arguments if you wish to run on different Profile, -Dspring.profiles.active=DEV");

            System.out.println("Current Default Profile = [DEV]");
            System.setProperty("spring.profiles.active", "DEV");
        }

        String dbIp = System.getProperty("db.ip");
        String dbPort = System.getProperty("db.port");
        String dbName = System.getProperty("db.name");
        String dbSchema = System.getProperty("db.schema");
        System.setProperty("db.url","jdbc:postgresql://192.18.141.9:5432/api_core");

        if (StringUtils.isEmpty(dbIp) || StringUtils.isEmpty(dbPort) || StringUtils.isEmpty(dbName) || StringUtils.isEmpty(dbSchema))
        {
            System.out.println("Please configure VM Arguments if you wish to connect to different Database, -Ddb.ip=192.18.141.9 -Ddb.port=5432 -Ddb.name=api_core -Ddb.schema=dev");
        }

      /*  String redisIp = System.getProperty("redis.ip");
        String redisPort = System.getProperty("redis.port");
        String redisPassword = System.getProperty("redis.password");

        if (StringUtils.isEmpty(redisIp) || StringUtils.isEmpty(redisPort) || StringUtils.isEmpty(redisPassword))
        {
            System.out.println("Please configure VM Arguments if you wish to connect to different Redis Server, -Dredis.ip=172.16.28.62 -Dredis.port=6379 -Dredis.password=123456");
        }*/

        // to avoid 2 same instance start in the same server
        System.setProperty("javamelody.storage-directory", "javamelody." + httpPort);
        System.setProperty("server.servlet.context-path", "/internal-api-ws");

        if(StringUtils.isNotEmpty(httpsPort))
        {
            System.setProperty("server.port", httpsPort);

            if(StringUtils.isNotEmpty(httpPort))
            {
                System.setProperty("server.http.port", httpPort);
            }
        }
        else
        {
            System.setProperty("server.port", httpPort);
        }

        //System.setProperty("spring.security.filter-order", "5");
        new SpringApplicationBuilder(InternalApiApplication.class).initializers().run(args);
    }

}
