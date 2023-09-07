/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avinvivo.sip.server.exception;

/**
 *
 * @author Kilian
 */
public class SubscriptionHeaderException extends Exception{
    public enum ErrorCode{
        MISSING_SUBJECT_HEADER("The subject header was missing in the response, Subscription is TERMINATED"),
        TERMINATED_STATE_HEADER("Client sent a TERMINATED subscription state. No further processing"),
        STATE_MISSMATCH("The state sent by the client does not match user state on the server. Subscription TERMINATED"),
        SUBJECT_HEADER_EMPTY("The subject header can't be empty"),
        INVALID_SUBJECT_MESSAGE_STRUCTURE("The subject message structure was invalid please do as the following example. MESSAGE_ID:{id}"),
        USER_MESSAGE_ID_MISSMATCH("A user tried to delete a message that did not belong to him"),
        MESSAGE_SUBJECT_WITHOUT_ID("User sent a message subject but did not provide a message ID"),
        UNKNOWN_SUBJECT("User sent an unknown subject to the server.");
        
        private final String label;

        private ErrorCode(String label){
            this.label = label;
        }
    }
    
    public SubscriptionHeaderException(ErrorCode code){
        super(code.label);
    }
    
    public SubscriptionHeaderException(ErrorCode code, String msg){
        super(code.name() +":" + code.label + ":" + msg);
    }
}
