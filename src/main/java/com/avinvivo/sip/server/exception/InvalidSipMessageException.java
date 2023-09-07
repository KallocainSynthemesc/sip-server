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
public class InvalidSipMessageException extends Exception {

    public enum ErrorCode {
        EMPTY_MESSAGE("The message body is empty and won't be processed."),
        DESERIALIZATION_FAILURE("Deserialization of the SIP message failed.");

        private final String label;

        private ErrorCode(String label) {
            this.label = label;
        }
    }

    public InvalidSipMessageException(ErrorCode code) {
        super(code.label);
    }

    public InvalidSipMessageException(ErrorCode code, String msg) {
        super(code.name() + ":" + code.label + ":" + msg);
    }
}
