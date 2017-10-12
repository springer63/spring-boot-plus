package com.github.boot.framework.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.boot.framework.jpa.MySQL5Dialect;
import com.github.boot.framework.jpa.dao.BaseDaoRepositoryFactoryBean;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.ValidationMode;
import javax.sql.DataSource;
import java.util.Properties;

/**
 * 数据库配置
 * @author cjh
 * @version
 * @date：2016年3月8日 下午12:09:35
 */
@SuppressWarnings("ALL")
@Configuration
@EnableTransactionManagement
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableJpaRepositories(basePackages = {"${jpa.dao.packages}"}, repositoryFactoryBeanClass = BaseDaoRepositoryFactoryBean.class)
public class DataSourceConfigure {

	@Autowired
	private Environment env;

	@Value("${db.driver-class:com.mysql.jdbc.Driver}")
	private String driverName;

	@Value("${db.url}")
	private String url;

	@Value("${db.username}")
	private String username;

	@Value("${db.password}")
	private String password;

	@Value("${db.init-size:50}")
	private int  initSize;

	@Value("${db.min-idle:10}")
	private int minIdle;

	@Value("${db.max-active:200}")
	private int maxActive;

	@Value("${db.max-wait:30000}")
	private int maxWait;

	/**
	 * 配置数据源
	 * @return
	 */
	@Bean
	public DataSource dataSource(){
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setUrl(url);
		dataSource.setDriverClassName(this.driverName);
		dataSource.setUsername(this.username);
		dataSource.setPassword(this.password);
		dataSource.setInitialSize(this.initSize);
		dataSource.setMaxActive(this.maxActive);
		dataSource.setMinIdle(this.minIdle);
		dataSource.setMaxWait(this.maxWait);
		dataSource.setTimeBetweenEvictionRunsMillis(6000);
		dataSource.setValidationQuery("SELECT 'X'");
		dataSource.setMinEvictableIdleTimeMillis(300000);
		dataSource.setTestOnBorrow(false);
		dataSource.setTestOnReturn(false);
		dataSource.setTestWhileIdle(true);
		dataSource.setPoolPreparedStatements(true);
		dataSource.setMaxPoolPreparedStatementPerConnectionSize(20);
		//dataSource.setFilters("wall,stat");
		return dataSource;
	}

	/**
	 * 配置 EntityManagerFactory
	 * @return
	 */
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(){
		LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
		entityManagerFactory.setValidationMode(ValidationMode.NONE);
		entityManagerFactory.setPersistenceUnitName("PERSISTENCE_UNIT");
		entityManagerFactory.setDataSource(dataSource());
		entityManagerFactory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		entityManagerFactory.setPackagesToScan(env.getProperty("jpa.entity.packages"));
		Properties jpaProperties = new Properties();
		jpaProperties.setProperty(AvailableSettings.SHOW_SQL, env.getProperty("jpa.show-sql", "false"));
		jpaProperties.setProperty(AvailableSettings.FORMAT_SQL,  env.getProperty("jpa.format-sql", "false"));
		jpaProperties.setProperty(AvailableSettings.HBM2DDL_AUTO, env.getProperty("jpa.ddl-auto", "none"));
		jpaProperties.setProperty(AvailableSettings.DIALECT, MySQL5Dialect.class.getName());
		jpaProperties.setProperty(AvailableSettings.DEFAULT_BATCH_FETCH_SIZE, "50");
		jpaProperties.setProperty(AvailableSettings.USE_NEW_ID_GENERATOR_MAPPINGS, "false");
		jpaProperties.setProperty(AvailableSettings.PHYSICAL_NAMING_STRATEGY, SpringPhysicalNamingStrategy.class.getName());
		entityManagerFactory.setJpaProperties(jpaProperties);
		return entityManagerFactory;
	}

	/**
	 * 配置事务管理器
	 * @return
	 */
	@Bean
	public JpaTransactionManager transactionManager(){
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
		return transactionManager;
	}



}
