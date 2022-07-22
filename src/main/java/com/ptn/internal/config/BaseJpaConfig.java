package com.ptn.internal.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
public abstract class BaseJpaConfig {
    private static Logger log = LoggerFactory.getLogger(BaseJpaConfig.class);

    public BaseJpaConfig() {
    }

    public abstract LocalContainerEntityManagerFactoryBean entityManagerFactory();

    public abstract DataSource dataSource();

    public abstract JpaVendorAdapter jpaAdapter();

    public Map<String, String> hibernateJpaPropertyMap() {
        HashMap map = new HashMap();
        map.put("hibernate.format_sql", "true");
        map.put("hibernate.use_sql_comments", "true");
        map.put("hibernate.hbm2ddl.auto", "update");
        map.put("hibernate.generate_statistics", "true");
        return map;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(this.entityManagerFactory().getObject());
        HashMap map = new HashMap();
        map.put("transactionTimeout", "43200");
        jpaTransactionManager.setJpaPropertyMap(map);
        return jpaTransactionManager;
    }

    public DataSource dataSource(String source) throws Exception {
        try {
            InitialContext initialContext = new InitialContext();
            DataSource dataSource = (DataSource)initialContext.lookup("java:/comp/env/" + source);
            return dataSource;
        } catch (Exception e) {
            log.warn("Datasource not loaded from Server... java:/comp/env/" + source);
            throw e;
        }
    }
}