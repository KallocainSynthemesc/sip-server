package com.avinvivo.sip.server.service;

import com.avinvivo.sip.server.bean.SipContentBody;
import com.avinvivo.sip.server.dao.CRUDable;
import com.avinvivo.sip.server.dao.SipContentBodyDao;
import java.util.List;

public class SipContentBodyService implements CRUDable<SipContentBody, Long> {

    private SipContentBodyDao dao = SipContentBodyDao.getInstance();

    private final static SipContentBodyService INSTANCE = new SipContentBodyService();

    private SipContentBodyService() {
    }

    public static SipContentBodyService getInstance() {
        return INSTANCE;
    }

    @Override
    public SipContentBody save(SipContentBody obj) {
        return this.dao.save(obj);
    }

    @Override
    public SipContentBody get(Long id) {
        return this.dao.get(id);
    }

    @Override
    public SipContentBody remove(Long id) {
        return this.dao.remove(id);
    }

    @Override
    public List<SipContentBody> select() {
        return this.dao.select();
    }

    @Override
    public void update(SipContentBody obj) {
        this.dao.update(obj);
    }

    public List<SipContentBody> selectAllByClient(String username) {
        return this.dao.selectAllByClient(username);

    }

    public SipContentBody getMessageForClient(String username, Long id) {
        return this.dao.getMessageForClient(username, id);
    }
}
