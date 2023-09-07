/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avinvivo.sip.server.processor.enums;

import com.avinvivo.sip.server.bean.SipUser;
import com.avinvivo.sip.server.exception.SubscriptionHeaderException;
import com.avinvivo.sip.server.exception.SubscriptionHeaderException.ErrorCode;
import com.avinvivo.sip.server.processor.state.StateBiConsumer;
import com.avinvivo.sip.server.processor.state.SubscriptionStateProcessor;
import javax.sip.header.SubscriptionStateHeader;

/**
 *
 * @author Kilian
 */
public enum SipSubscriptionStateType {
    
    ACTIVE(SubscriptionStateProcessor::processActive),
    PENDING(SubscriptionStateProcessor::processPending),
    TERMINATED(SubscriptionStateProcessor::processTerminated);

    public final StateBiConsumer<SubscriptionStateProcessor, SipUser> compute;

    private SipSubscriptionStateType(StateBiConsumer<SubscriptionStateProcessor, SipUser> compute)
            throws ExceptionInInitializerError {
        this.compute = compute;
    }
    
    public static SipSubscriptionStateType fromStates(SipUser user, SubscriptionStateHeader sstate) throws SubscriptionHeaderException {
        final boolean clientSentHeader = sstate != null;
        final String userState = user.getSubsriptionState().toUpperCase();
        if (clientSentHeader) {
            final String upperSstate = sstate.getState().toUpperCase();
            if (upperSstate.equals(userState) == false) {
                if("TERMINATED".equals(upperSstate)){
                    return SipSubscriptionStateType.TERMINATED;
                }
                throw new SubscriptionHeaderException(ErrorCode.STATE_MISSMATCH);
            }
        }
        return SipSubscriptionStateType.valueOf(userState);
    }
}
