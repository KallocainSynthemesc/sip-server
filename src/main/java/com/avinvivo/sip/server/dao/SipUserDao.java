package com.avinvivo.sip.server.dao;

import com.avinvivo.sip.server.bean.SipUser;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class SipUserDao implements CRUDable<SipUser, String> {

    private final static SipUserDao instance = new SipUserDao();

    private static final ConcurrentMap<String, SipUser> USERNAME_TO_OBJECT = new ConcurrentHashMap<>();

    public final static SipUserDao getInstance() {
        return instance;
    }

    @Override
    public SipUser save(SipUser obj){
        USERNAME_TO_OBJECT.putIfAbsent(obj.getUser_name(), obj);
        return obj;
    }

    @Override
    public SipUser get(String id) {
        return USERNAME_TO_OBJECT.get(id);
    }

    public SipUser getByAddress(String adress) {
        return USERNAME_TO_OBJECT.values().stream()
		.filter(user -> adress.equals(user.getSipadresse()))
		.findAny()
		.orElse(null);
    }

    @Override
    public SipUser remove(String id) {
        return USERNAME_TO_OBJECT.remove(id);
    }

    @Override
    public Collection<SipUser> select() {
        return USERNAME_TO_OBJECT.values();

    }

    @Override
    public void update(SipUser obj) {
        USERNAME_TO_OBJECT.replace(obj.getUser_name(), obj);
    }
    
    public boolean isSipAddresseUserUnique(SipUser obj) {
	final AtomicBoolean usernameEqual = new AtomicBoolean(false);
	final AtomicBoolean sipAddresseEqual = new AtomicBoolean(false);
	USERNAME_TO_OBJECT.forEach((username, sipUser) -> {
	    sipAddresseEqual.set(obj.getSipadresse().equals(sipUser.getSipadresse()));
	    usernameEqual.set(username.equals(obj.getUser_name()));
	});
	return usernameEqual.get() == sipAddresseEqual.get();
    }
}
