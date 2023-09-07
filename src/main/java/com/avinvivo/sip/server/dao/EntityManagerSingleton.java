package com.avinvivo.sip.server.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import static javax.persistence.Persistence.createEntityManagerFactory;

public class EntityManagerSingleton {
    
    private final static EntityManagerSingleton INSTANCE = new EntityManagerSingleton();

    public static EntityManagerSingleton getInstance() {
        return INSTANCE;
    }

    private EntityManager entityManager = null;
    
    public EntityManagerSingleton() {
        EntityManagerFactory emf = createEntityManagerFactory("camlJmsSip");
        this.entityManager = emf.createEntityManager();
    }

    public EntityManager getEntityManager() {
        return this.entityManager;
    }

    public void closeEntityManager() {
        if (this.entityManager.isOpen()) {
            this.entityManager.close();            
        }
    }
}
