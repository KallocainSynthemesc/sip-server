package com.avinvivo.sip.server.bean;

import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

public class PreventAnyUpdate {

    @PreUpdate
    void onPreUpdate(Object o) {
        throw new IllegalStateException("JPA is trying to update an entity of type " + (o == null ? "null" : o.getClass()));
    }

    @PreRemove
    void onPreRemove(Object o) {
        throw new IllegalStateException("JPA is trying to remove an entity of type " + (o == null ? "null" : o.getClass()));
    }
}