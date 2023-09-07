/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avinvivo.sip.server.bean;

import com.avinvivo.sip.server.exception.SubscriptionHeaderException;
import com.avinvivo.sip.server.exception.SubscriptionHeaderException.ErrorCode;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Kilian
 */
public class SipSubjectTest {
    
    public SipSubjectTest() {
    }

    /**
     * Test of toString method, of class SipSubject.
     */
//    @org.junit.jupiter.api.Test
//    public void testToString() {
//        System.out.println("toString");
//        SipSubject instance = null;
//        String expResult = "";
//        String result = instance.toString();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of fromString method, of class SipSubject.
     */
    @org.junit.jupiter.api.Test
    public void testFromStringMessageIdCorrect() throws SubscriptionHeaderException {
        System.out.println("testFromStringMessageIdCorrect");
        String subject = SipSubject.SipSubjectType.MESSAGE_ID + ":" + 1;
        String expResult = "MESSAGE_ID:1";
        SipSubject result = SipSubject.fromString(subject);
        assertEquals(expResult, result.toString());
    }
    
    @org.junit.jupiter.api.Test
    public void testFromStringSubscribeRefreshCorrect() throws SubscriptionHeaderException {
        System.out.println("testFromStringSubscribeRefreshCorrect");
        String subject = SipSubject.SipSubjectType.SUBSCRIBE_REFRESH.name();
        String expResult = "SUBSCRIBE_REFRESH";
        SipSubject result = SipSubject.fromString(subject);
        assertEquals(expResult, result.toString());
    }
    /*this is a case that is probably never going to happen because we need 
    this type only to send an error notification to the client when a subject 
    header is missing. Would be weird if the client fails to send a subject header, 
    but starts to send a subject header on the response to an error notify that
    the subject header is missing. However we leave the type in because it is handy
    to build a erro subject message with it.*/
    @org.junit.jupiter.api.Test
    public void testFromStringMissingSubjectCorrect() throws SubscriptionHeaderException { 
        System.out.println("testFromStringMissingSubjectCorrect");
        String subject = SipSubject.SipSubjectType.MISSING_SUBJECT.name();
        String expResult = "MISSING_SUBJECT";
        SipSubject result = SipSubject.fromString(subject);
        assertEquals(expResult, result.toString());
    }
    
    @org.junit.jupiter.api.Test
    public void testFromStringNullMessage() {
        System.out.println("testFromStringNullMessage");
        SubscriptionHeaderException thrown = Assertions.assertThrows(SubscriptionHeaderException.class, () -> {
            SipSubject result = SipSubject.fromString("");
        });
        SubscriptionHeaderException ex = new SubscriptionHeaderException(ErrorCode.SUBJECT_HEADER_EMPTY);
        Assertions.assertEquals(ex.getMessage(), thrown.getMessage());
    }
    
    @org.junit.jupiter.api.Test
    public void testFromStringMissingID() {
        System.out.println("testFromStringMissingID");
        String subject = SipSubject.SipSubjectType.MESSAGE_ID.name() + ":";
        SubscriptionHeaderException thrown = Assertions.assertThrows(SubscriptionHeaderException.class, () -> {
            SipSubject result = SipSubject.fromString(subject);
        });
        SubscriptionHeaderException ex = new SubscriptionHeaderException(ErrorCode.INVALID_SUBJECT_MESSAGE_STRUCTURE);
        Assertions.assertEquals(ex.getMessage(), thrown.getMessage());
    }
    
    @org.junit.jupiter.api.Test
    public void testFromStringInvalidMessage() {
        System.out.println("testFromStringInvalidMessage");
        String subject = SipSubject.SipSubjectType.MESSAGE_ID.name();
        SubscriptionHeaderException thrown = Assertions.assertThrows(SubscriptionHeaderException.class, () -> {
            SipSubject result = SipSubject.fromString(subject);
        });
        SubscriptionHeaderException ex = new SubscriptionHeaderException(ErrorCode.MESSAGE_SUBJECT_WITHOUT_ID);
        Assertions.assertEquals(ex.getMessage(), thrown.getMessage());
    }
    
    @org.junit.jupiter.api.Test
    public void testFromStringNonExistingSubject() {
        System.out.println("testFromStringNonExistingSubject");
        String subject = "NONEXISTING_SUBJECT";
        SubscriptionHeaderException thrown = Assertions.assertThrows(SubscriptionHeaderException.class, () -> {
            SipSubject result = SipSubject.fromString(subject);
        });
        SubscriptionHeaderException ex = new SubscriptionHeaderException(ErrorCode.UNKNOWN_SUBJECT);
        Assertions.assertTrue(thrown.getMessage().contains(ex.getMessage()));
    }
}