package com.stackabuse.kafka_data_pipeline.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Objects;

@Configuration
@PropertySource({ "classpath:persistence-multiple-db.properties" })
@EnableJpaRepositories(
        basePackages = "com.stackabuse.kafka_data_pipeline.mysql.repository",
        entityManagerFactoryRef = "mysqlEntityManagerFactory",
        transactionManagerRef = "mysqlTransactionManager"
)
public class MySQLConfig {

    @Autowired
    private Environment environment;

    @Bean
    public LocalContainerEntityManagerFactoryBean mysqlEntityManagerFactory() {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(mysqlDataSource());
        em.setPackagesToScan("com.stackabuse.kafka_data_pipeline.model");
        em.setPersistenceUnitName("model");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.afterPropertiesSet();

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", environment.getProperty("hibernate.hbm2ddl.auto"));
        properties.put("hibernate.generate-ddl", environment.getProperty("hibernate.generate-ddl"));
        properties.put("hibernate.show_sql", environment.getProperty("hibernate.show_sql"));
        properties.put("hibernate.dialect", environment.getProperty("mysql.jdbc.dialect"));
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean
    public DataSource mysqlDataSource() {

        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(
                Objects.requireNonNull(environment.getProperty("mysql.jdbc.driverClassName"))
        );
        dataSource.setUrl(environment.getProperty("mysql.jdbc.url"));
        dataSource.setUsername(environment.getProperty("mysql.jdbc.user"));
        dataSource.setPassword(environment.getProperty("mysql.jdbc.pass"));

        return dataSource;
    }

    @Bean
    public PlatformTransactionManager mysqlTransactionManager(EntityManagerFactory entityManagerFactory) {

        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(mysqlEntityManagerFactory().getObject());
        return transactionManager;
    }
}
