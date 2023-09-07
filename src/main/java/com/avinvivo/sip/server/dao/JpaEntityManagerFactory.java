/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avinvivo.sip.server.dao;

import com.avinvivo.sip.server.bean.Configuration;
import com.avinvivo.sip.server.bean.SipContentBody;
import com.mysql.cj.jdbc.MysqlDataSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.sql.DataSource;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitInfo;
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.hibernate.jpa.boot.internal.PersistenceUnitInfoDescriptor;
/**
 *
 * @author Kilian
 */
public class JpaEntityManagerFactory {
    private final String DB_URL = "jdbc:mysql://localhost:3306/sip?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    private final String DB_USER_NAME = "sip";
    private final String DB_PASSWORD = "_sip-31";
    
    private final static JpaEntityManagerFactory INSTANCE = new JpaEntityManagerFactory();

    public static JpaEntityManagerFactory getInstance() {
        return INSTANCE;
    }
    
    public EntityManager getEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }
    
    protected EntityManagerFactory getEntityManagerFactory() {
        PersistenceUnitInfo persistenceUnitInfo = getPersistenceUnitInfo(getClass().getSimpleName());
        Map<String, Object> configuration = new HashMap<>();
        return new EntityManagerFactoryBuilderImpl(new PersistenceUnitInfoDescriptor(persistenceUnitInfo), configuration)
          .build();
    }
    
    protected PersistenceUnitInfoImpl getPersistenceUnitInfo(String name) {
        return new PersistenceUnitInfoImpl(name, getEntityClassNames(), getProperties());
    }
    
    protected List<String> getEntityClassNames() {
        return Arrays.asList(getEntities())
          .stream()
          .map(Class::getName)
          .collect(Collectors.toList());
    }
    
    protected Properties getProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
        properties.put("hibernate.id.new_generator_mappings", false);
        properties.put("hibernate.connection.datasource", getMysqlDataSource());
        properties.put("hibernate.connection.autocommit", true);
        properties.put("hibernate.validator.apply_to_ddl", true);
        properties.put("hibernate.format_sql", false);
        properties.put("hibernate.show_sql", true);
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.transaction.jta.platform", "org.hibernate.service.jta.platform.internal.SunOneJtaPlatform");
        properties.put("javax.persistence.schema-generation.database.action", "update");
        return properties;
    }
    
    protected Class[] getEntities() {
        return new Class[]{Configuration.class, SipContentBody.class};
    }
    
    protected DataSource getMysqlDataSource() {
	MysqlDataSource mysqlDataSource = new MysqlDataSource();
        mysqlDataSource.setURL(DB_URL);
	mysqlDataSource.setUser(DB_USER_NAME);
        mysqlDataSource.setPassword(DB_PASSWORD);
	return mysqlDataSource;
    }
}
