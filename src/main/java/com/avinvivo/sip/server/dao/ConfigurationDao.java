package com.avinvivo.sip.server.dao;

import javax.persistence.EntityManager;
import com.avinvivo.sip.server.bean.Configuration;
import com.avinvivo.sip.server.bean.Configuration_;

public class ConfigurationDao extends AbstractCrudable<Configuration, Long> implements CRUDable<Configuration, Long> {

    public final static String OAUTH_TOKEN_ENDPOINT = "OAUTH_TOKEN_ENDPOINT";
    public final static String OAUTH_CLIENT_NAME = "OAUTH_CLIENT_NAME";
    public final static String OAUTH_CLIENT_PASSWORD = "OAUTH_CLIENT_PASSWORD";
    public final static String OAUTH_RECEIVE_TOKEN_ENDPOINT ="OAUTH_RECEIVE_TOKEN_ENDPOINT";
    public final static String PUBLISH_USER = "PUBLISH_USER";
    
    private final EntityManager em = JpaEntityManagerFactory.getInstance().getEntityManager();
    private final static ConfigurationDao instance = new ConfigurationDao();

    public final static ConfigurationDao getInstance() {
        return instance;
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    @Override
    protected Class<Configuration> getClassType() {
        return Configuration.class;
    }
    
    public boolean exist(String name) {
        return super.exist(Configuration.class, Configuration_.name, name);
    }
}
