/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avinvivo.sip.server.processor.state;

import com.avinvivo.sip.server.bean.SipContentBody;
import com.avinvivo.sip.server.bean.SipSubject;
import com.avinvivo.sip.server.bean.SipUser;
import com.avinvivo.sip.server.exception.SubscriptionHeaderException;
import static com.avinvivo.sip.server.exception.SubscriptionHeaderException.ErrorCode;
import com.avinvivo.sip.server.listener.CustomSipPresenceAgent;
import com.avinvivo.sip.server.processor.enums.SipSubscriptionStateType;
import com.avinvivo.sip.server.processor.publish.PendingNotificationProcessor;
import com.avinvivo.sip.server.service.SipContentBodyService;
import com.avinvivo.sip.server.service.SipUserService;
import gov.nist.javax.sip.message.SIPResponse;
import java.text.ParseException;
import javax.sip.SipException;
import javax.sip.SipProvider;
import javax.sip.header.SubjectHeader;
import static javax.sip.header.SubscriptionStateHeader.ACTIVE;
import org.apache.camel.component.sip.SipConfiguration;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;

/**
 *
 * @author Kilian
 */
public class SubscriptionStateProcessor {
    
    private static final Logger LOG = getLogger(SubscriptionStateProcessor.class);
    private final SipUserService userService = SipUserService.getInstance();
    private final SipContentBodyService messageService = SipContentBodyService.getInstance();
    private final CustomSipPresenceAgent sipPresenceAgent;
    private final SIPResponse response;
    
    public SubscriptionStateProcessor(final CustomSipPresenceAgent sipPresenceAgent, final SIPResponse response){
        this.sipPresenceAgent = sipPresenceAgent;
        this.response = response;
    }
    
    public void computeRequest(SipSubscriptionStateType type, SipUser user) throws SubscriptionHeaderException {
        type.compute.accept(this, user);
    }
    
    public final void processActive(final SipUser user) throws SubscriptionHeaderException {
        SubjectHeader subjHeader = (SubjectHeader) this.response.getHeader(SubjectHeader.NAME);
        if (subjHeader != null) {
            SipSubject subject = SipSubject.fromString(subjHeader.getSubject());
            LOG.debug("Sucessuflly received response with subject header from a notify of type "+subject.getType());
            if(subject.getType() == SipSubject.SipSubjectType.MESSAGE_ID)
            {
                SipContentBody message = messageService.getMessageForClient(user.getUser_name(), subject.getId());
                if(message != null)
                {
                    messageService.remove(message.getId());
                    LOG.debug("Removed pending message (" + subject.getId() + ") for client: " + user.getClient_id());
                }
                else{
                    throw new SubscriptionHeaderException(ErrorCode.USER_MESSAGE_ID_MISSMATCH);
                }
            }
        } else {
            throw new SubscriptionHeaderException(ErrorCode.MISSING_SUBJECT_HEADER);
        }
    }

    public final void processPending(SipUser user) throws ParseException, SipException {
        user.setSubsriptionState(ACTIVE);
        userService.update(user);
        PendingNotificationProcessor pendingNotifier = new PendingNotificationProcessor(getConfig(), getProvider(), user);
        pendingNotifier.process();
        LOG.debug("Send pending messages for client " + user.getClient_id());
    }
    
    public final void processTerminated(SipUser user) throws SubscriptionHeaderException{
        LOG.debug("Client "+user.getClient_id()+" sent TERMINATED in the Response.");
        throw new SubscriptionHeaderException(ErrorCode.TERMINATED_STATE_HEADER);
    }
    
    private final SipConfiguration getConfig() {
        return this.sipPresenceAgent.getConfiguration();
    }

    private final SipProvider getProvider() {
        return this.sipPresenceAgent.getProvider();
    }
}
