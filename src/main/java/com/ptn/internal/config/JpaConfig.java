package com.ptn.internal.config;

import net.bull.javamelody.JdbcWrapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
public class JpaConfig extends BaseJpaConfig {
	private static Logger log = LoggerFactory.getLogger(JpaConfig.class);

	@Autowired
	Environment env;

	@Primary
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
		entityManagerFactoryBean.setDataSource(this.dataSource());
		entityManagerFactoryBean.setJpaVendorAdapter(this.jpaAdapter());
		
		Map<String, String> propertyMap = this.hibernateJpaPropertyMap();
		
        // set this, so don't scan METADATA
        // this will break, if we using AUTO ID INCREMENTAL.
		propertyMap.put("hibernate.temp.use_jdbc_metadata_defaults","false");

        entityManagerFactoryBean.setJpaPropertyMap(propertyMap);
        

		// find and register all @Entity classes within
		entityManagerFactoryBean.setPackagesToScan("com.ptn.internal.model");

		return entityManagerFactoryBean;
	}

	@Primary
	@Bean
	public DataSource dataSource() {
		String activeProfile = getActiveProfie();
		try {
			log.debug("Loading datasource configuration for: [" + activeProfile + "]");
			DataSource ds = super.dataSource("jdbc/DB_" + activeProfile);
			
			return ds;
		} catch (Exception e) {
			log.error("Datasource 'jdbc/DB_" + activeProfile + "' not loaded from Server, now load from JDBC ...");

			org.apache.tomcat.jdbc.pool.DataSource ds = new org.apache.tomcat.jdbc.pool.DataSource();
			ds.setDriverClassName("org.postgresql.Driver");

			String ip = System.getProperty("db.ip");
			String port = System.getProperty("db.port");
			String name = System.getProperty("db.name");
			String schema = System.getProperty("db.schema");
			String username = System.getProperty("db.username");
			String password = System.getProperty("db.password");
			String dbType = System.getProperty("db.type");
			
			if ("tidb".equals(dbType) || "mysql".equals(dbType)) {
				ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
				if (StringUtils.isNotEmpty(ip) && StringUtils.isNotEmpty(port) && StringUtils.isNotEmpty(name)
						&& StringUtils.isNotEmpty(schema)) {
					ds.setUrl("jdbc:mysql://" + ip + ":" + port + "/" + name + "?currentSchema=" + schema);
				} else {
					log.warn("Fallback to local pooled datasource");
					ds.setUrl("jdbc:mysql://localhost:5432/");
				}
			} else {
				ds.setDriverClassName("org.postgresql.Driver");
				if (StringUtils.isNotEmpty(ip) && StringUtils.isNotEmpty(port) && StringUtils.isNotEmpty(name)
						&& StringUtils.isNotEmpty(schema)) {
					ds.setUrl("jdbc:postgresql://" + ip + ":" + port + "/" + name + "?currentSchema=" + schema);
				} else {
					log.debug("Fallback to local pooled datasource");
					ds.setUrl("jdbc:postgresql://localhost:5432/");
				}
			}
			
			log.error("JDBC URL = '" + ds.getUrl() + "'");

			if (StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(password)) {
				ds.setUsername(username);
				ds.setPassword(password);
			} else {
				ds.setUsername("postgres");
				ds.setPassword("postgres");
			}
			
			// extra tomcat jdbc properties
			ds.setInitialSize(1);
			ds.setMinIdle(1);
			ds.setMaxIdle(2);
			ds.setRemoveAbandonedTimeout(600000);
			ds.setRemoveAbandoned(true);
			ds.setDefaultTransactionIsolation(java.sql.Connection.TRANSACTION_READ_UNCOMMITTED);
			ds.setValidationQuery("SELECT 1");
			ds.setTestOnBorrow(true); // To check connection using validation query when getting connection from pool

			return JdbcWrapper.SINGLETON.createDataSourceProxy(ds);
		}
	}

	@Bean
	public JpaVendorAdapter jpaAdapter() {
		HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();

		String dbType = System.getProperty("db.type");
		if ("tidb".equals(dbType) || "mysql".equals(dbType)) {
			adapter.setDatabase(Database.MYSQL);
			adapter.setDatabasePlatform("org.hibernate.dialect.MySQL8Dialect");
		} else {
			adapter.setDatabase(Database.POSTGRESQL);
			adapter.setDatabasePlatform("org.hibernate.dialect.PostgreSQL94Dialect");
		}

		return adapter;
	}

	@Bean
	public String dbSequence() {
        // MYSQL
    	String dbType = System.getProperty("db.type");
    	
    	if("tidb".equals(dbType) || "mysql".equals(dbType))
        {
    		return "SELECT NEXTVAL (?) AS SEQ";
        }
        else
        {
        	return "SELECT NEXTVAL ('?') AS SEQ";
        }    	
    }

	@Bean
	public String spGenSequence() {
		// Stored Proc Name
		return "SP_GENERATE_SEQ_NO_BIGINT";
	}

	@Bean
	public JdbcTemplate jdbcTemplate() {
		return new JdbcTemplate(dataSource());
	}

	@Bean
	@Primary
	@Qualifier("transactionManager")
	public PlatformTransactionManager transactionManager() {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(this.entityManagerFactory().getObject());
		HashMap<String, String> jpaProperties = new HashMap<String, String>();
		jpaProperties.put("transactionTimeout", "43200");
		transactionManager.setJpaPropertyMap(jpaProperties);
		return transactionManager;
	}
	
	private String getActiveProfie() {
		String[] activeProfiles = env.getActiveProfiles();

		for (String profile : activeProfiles) {
			if ("DEV".equals(profile) || "SIT".equals(profile) || "UAT".equals(profile) || "DR".equals(profile)
					|| "PROD".equals(profile)) {
				return profile;
			}
		}
		return null;
	}
}