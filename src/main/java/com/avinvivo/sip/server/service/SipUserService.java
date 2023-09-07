package com.avinvivo.sip.server.service;

import com.avinvivo.sip.server.bean.SipUser;
import com.avinvivo.sip.server.dao.CRUDable;
import com.avinvivo.sip.server.dao.SipUserDao;
import java.util.Collection;

public class SipUserService implements CRUDable<SipUser, String> {

    private SipUserDao dao = SipUserDao.getInstance();

    private final static SipUserService INSTANCE = new SipUserService();

    private SipUserService() {
    }

    public static SipUserService getInstance() {
        return INSTANCE;
    }

    @Override
    public SipUser save(SipUser obj) {
        return this.dao.save(obj);
    }

    @Override
    public SipUser get(String id) {
        return this.dao.get(id);
    }

    @Override
    public SipUser remove(String id) {
        return this.dao.remove(id);
    }

    @Override
    public Collection<SipUser> select() {
        return this.dao.select();
    }

    @Override
    public void update(SipUser obj) {
        this.dao.update(obj);
    }
    
    public SipUser getByAddress(String addresse) {
	return this.dao.getByAddress(addresse);
    }
	
    public boolean isSipAddresseUserUnique(SipUser obj) {
	return this.dao.isSipAddresseUserUnique(obj);
    }
}
