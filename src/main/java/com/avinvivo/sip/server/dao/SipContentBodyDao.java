package com.avinvivo.sip.server.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.avinvivo.sip.server.bean.SipContentBody;

public class SipContentBodyDao extends AbstractCrudable<SipContentBody, Long> implements CRUDable<SipContentBody, Long> {

    private final static SipContentBodyDao instance = new SipContentBodyDao();

    private final EntityManager em = JpaEntityManagerFactory.getInstance().getEntityManager();

    public final static SipContentBodyDao getInstance() {
        return instance;
    }

    public List<SipContentBody> selectAllByClient(String username) {
        CriteriaBuilder builder = this.em.getCriteriaBuilder();
        CriteriaQuery<SipContentBody> criteriaQuery = builder.createQuery(SipContentBody.class);
        Root<SipContentBody> root = criteriaQuery.from(SipContentBody.class);
        criteriaQuery.where(
                builder.equal(
                        root.get("username"), username
                )
        );

        return this.em.createQuery(criteriaQuery).getResultList();

    }
    
    public SipContentBody getMessageForClient(String username, Long id) {
        CriteriaBuilder builder = this.em.getCriteriaBuilder();
        CriteriaQuery<SipContentBody> criteriaQuery = builder.createQuery(SipContentBody.class);
        Root<SipContentBody> root = criteriaQuery.from(SipContentBody.class);
        criteriaQuery.where(
                builder.and(
                    builder.equal(
                            root.get("username"), username
                    ),builder.equal(
                            root.get("id"), id
                    )
                )
        );

        return this.em.createQuery(criteriaQuery).getSingleResult();

    }

    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    @Override
    protected Class<SipContentBody> getClassType() {
        return SipContentBody.class;
    }

}
