/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avinvivo.sip.server.dao.datainitialization;

import com.avinvivo.sip.server.bean.Configuration;
import com.avinvivo.sip.server.dao.ConfigurationDao;
import static com.avinvivo.sip.server.dao.ConfigurationDao.OAUTH_CLIENT_NAME;
import static com.avinvivo.sip.server.dao.ConfigurationDao.OAUTH_CLIENT_PASSWORD;
import static com.avinvivo.sip.server.dao.ConfigurationDao.OAUTH_TOKEN_ENDPOINT;
import static com.avinvivo.sip.server.dao.ConfigurationDao.OAUTH_RECEIVE_TOKEN_ENDPOINT;
import static com.avinvivo.sip.server.dao.ConfigurationDao.PUBLISH_USER;
import javax.annotation.PostConstruct;

public class ConfigurationDataInitService {
    
    private final ConfigurationDao dao = ConfigurationDao.getInstance();
    
    @PostConstruct
    public void load() {
        this.save(OAUTH_TOKEN_ENDPOINT, "http://localhost:8080/bimi-authorization/oauth/check_token");
        this.save(OAUTH_CLIENT_NAME, "sip-server");
        this.save(OAUTH_CLIENT_PASSWORD, "123");
        this.save(OAUTH_RECEIVE_TOKEN_ENDPOINT, "http://localhost:8080/bimi-authorization/oauth/token");
        this.save(PUBLISH_USER, "glassfishqueue");
    }

    protected void save(String key, String value) {
        if(this.dao.exist(key) == false)
        {
            Configuration conf = new Configuration(key, value);
            this.dao.save(conf);
        }
    }
}
