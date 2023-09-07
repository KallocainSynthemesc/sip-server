/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avinvivo.sip.server.bean;

import com.avinvivo.sip.server.exception.SubscriptionHeaderException;
import com.avinvivo.sip.server.exception.SubscriptionHeaderException.ErrorCode;

/**
 *
 * @author Kilian
 */
public final class SipSubject {
    private final Long id;
    private SipSubjectType type;
    
    public SipSubject(SipSubjectType type, final Long id){
        this.type = type;
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public SipSubjectType getType() {
        return type;
    }

    @Override
    public String toString() {
        if(id != null){
            return type.name() + ":" + id;
        }
        return type.name();
    }
    
    public static SipSubject fromString(final String subject) throws SubscriptionHeaderException {
        if ("".equals(subject)) {
            throw new SubscriptionHeaderException(ErrorCode.SUBJECT_HEADER_EMPTY);
        }
        if (subject.contains(":")) {
            return fromConcatinatedSubject(subject);
        }

        if (SipSubjectType.MESSAGE_ID.name().equals(subject)) {
            throw new SubscriptionHeaderException(ErrorCode.MESSAGE_SUBJECT_WITHOUT_ID);
        }

        try {
            SipSubjectType type = SipSubjectType.valueOf(subject);
            return new SipSubject(type, null);
        } catch (IllegalArgumentException e) {
            throw new SubscriptionHeaderException(ErrorCode.UNKNOWN_SUBJECT, e.getMessage());
        }
    }
    
    private static SipSubject fromConcatinatedSubject(String subject) throws SubscriptionHeaderException {
        String[] parts = subject.split(":");
        if (parts.length > 1) {
            try {
                SipSubjectType type = SipSubjectType.valueOf(parts[0]);
                return new SipSubject(type, Long.parseLong(parts[1]));
            } catch (IllegalArgumentException ex) {
                throw new SubscriptionHeaderException(ErrorCode.INVALID_SUBJECT_MESSAGE_STRUCTURE, ex.getMessage());
            }
        }
        throw new SubscriptionHeaderException(ErrorCode.INVALID_SUBJECT_MESSAGE_STRUCTURE);
    }
    
    public enum SipSubjectType {
        SUBSCRIBE_REFRESH,
        MESSAGE_ID,
        MISSING_SUBJECT,
        SUBSCRIBE_INITIALIZE,
        SUBSCRIBE_TERMINATE;
    }
}
