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
public final class AuthException extends Exception {
          
    public enum ErrorCode{
        USER_MISSMATCH_ERROR("SIP user is not the same as the oauth2 user in the token"),
        MISSING_AUTH_HEADER_ERROR("The Authentication Header is missing in the SIP request"),
        MISSING_TOKEN("The Token in the Authentication Header is missing"),
        TOKEN_VERIFICATION_FAILED("Calling the Token verification endpoint caused an exception, could not authenticate the user"),
        VERIFIED_USER_INACTIVE("The user verfied by the token endpoint is inactive and therefore not authorized"),
        UNKOWN_PUBLISH_USER("The user is not authorized to make a publish request"),
        PUBLISH_REQUEST_INVALID_HOST("Publish request is from an invalid host"),
	USERNAME_SIP_ADDRESS_IN_USE("The user you are trying to subscribe with is already subscribed with a different SIP addresse");
        
        private final String label;

        private ErrorCode(String label){
            this.label = label;
        }
    }
    
    public AuthException(ErrorCode code){
        super(code.label);
    }
    
    public AuthException(ErrorCode code, String msg){
        super(code.name() +":" + code.label + ":" + msg);
    }
}
