/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avinvivo.sip.server.dao;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;

/**
 *
 * @author Kilian
 */
public abstract class AbstractCrudable<T, PK> implements CRUDable<T, PK>{
    
    @Override
    public T save(T obj) {
        try {
            getEntityManager().getTransaction().begin();
            getEntityManager().persist(obj);
            getEntityManager().getTransaction().commit();
            return obj;
        } catch (Throwable e) {
            if (getEntityManager().getTransaction().isActive()) {
                getEntityManager().getTransaction().rollback();
            }
            throw e;
        }
    }

    @Override
    public T get(PK id) {
        T obj = getEntityManager().find(getClassType(), id);
        return obj;
    }

    @Override
    public T remove(PK id) {
        try {
            getEntityManager().getTransaction().begin();
            T obj = this.get(id);
            getEntityManager().remove(obj);
            getEntityManager().getTransaction().commit();
            return obj;
        } catch (Throwable e) {
            if (getEntityManager().getTransaction().isActive()) {
                getEntityManager().getTransaction().rollback();
            }
            throw e;
        }
    }

    @Override
    public List<T> select() {
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = builder.createQuery(getClassType());
        Root<T> root = criteriaQuery.from(getClassType());
        criteriaQuery.select(root);

        return getEntityManager().createQuery(criteriaQuery).getResultList();

    }

    @Override
    public void update(T obj) {
        try {
            getEntityManager().getTransaction().begin();
            getEntityManager().merge(obj);
            getEntityManager().getTransaction().commit();
        } catch (Throwable e) {
            if (getEntityManager().getTransaction().isActive()) {
                getEntityManager().getTransaction().rollback();
            }
            throw e;
        }
    }

    public T selectByName(String client) {
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = builder.createQuery(getClassType());
        Root<T> root = criteriaQuery.from(getClassType());
        criteriaQuery.where(
                builder.equal(
                        root.get("name"), client
                )
        );

        return getEntityManager().createQuery(criteriaQuery).getSingleResult();
    }
    
    protected <T, V> boolean exist(Class<T> t, SingularAttribute<T, V> attribute, V v) {
        return count(t, attribute, v) > 0L;
    }
    
    private final <T, V> Long count(Class<T> t, SingularAttribute<T, V> attribute, V v) {
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> query = countQuery(builder, t);
        Root<T> root = (Root<T>) query.getRoots().iterator().next();
        query.where(builder.equal(root.get(attribute), v));
        return getEntityManager().createQuery(query).getSingleResult();
    }
    
    private final <T> CriteriaQuery<Long> countQuery(CriteriaBuilder builder, Class<T> t) {
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<T> root = query.from(t);
        query.select(builder.count(root));
        return query;
    }
    
    protected abstract EntityManager getEntityManager();
    
    protected abstract Class<T> getClassType();
}
