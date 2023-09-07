package com.avinvivo.sip.server.processor;

import com.avinvivo.sip.server.processor.enums.SipResponseType;
import com.avinvivo.sip.server.bean.SipUser;
import com.avinvivo.sip.server.listener.CustomSipPresenceAgent;
import com.avinvivo.sip.server.processor.enums.SipSubscriptionStateType;
import com.avinvivo.sip.server.processor.publish.ErrorNotificationProcessor;
import com.avinvivo.sip.server.processor.state.SubscriptionStateProcessor;
import com.avinvivo.sip.server.service.SipUserService;
import javax.sip.ResponseEvent;
import javax.sip.header.FromHeader;
import javax.sip.header.ToHeader;
import javax.sip.message.Response;

import org.slf4j.Logger;

import gov.nist.javax.sip.message.SIPResponse;
import java.text.ParseException;
import javax.sip.SipException;
import javax.sip.header.SubscriptionStateHeader;
import static org.apache.camel.component.sip.listener.SipMessageCodes.SIP_MESSAGE_CODES;
import static org.slf4j.LoggerFactory.getLogger;

public final class ResponseMessageProcessor {

    private static final Logger LOG = getLogger(ResponseMessageProcessor.class);
    private final CustomSipPresenceAgent sipPresenceAgent;
    private final SipUserService userService = SipUserService.getInstance();

    public ResponseMessageProcessor(final CustomSipPresenceAgent sipPresenceAgent) {
        this.sipPresenceAgent = sipPresenceAgent;
    }

    public final void computeResponse(SipResponseType type, ResponseEvent response) {
        type.compute.accept(this, response);
    }

    public final void processOk(ResponseEvent responseEvent) {
        SIPResponse response = (SIPResponse) responseEvent.getResponse();
        ToHeader toHeader = (ToHeader) response.getHeader(ToHeader.NAME);
        final String userName = toHeader.getAddress().getURI().toString();

        final SubscriptionStateProcessor stateService = new SubscriptionStateProcessor(sipPresenceAgent, response);
        SipUser user = userService.getByAddress(userName);
        if (user != null) {
            try {
                SubscriptionStateHeader sstate = (SubscriptionStateHeader) response.getHeader(SubscriptionStateHeader.NAME);
                SipSubscriptionStateType type = SipSubscriptionStateType.fromStates(user, sstate);
                stateService.computeRequest(type, user);
            } catch (Throwable e) {
                userService.remove(user.getUser_name());
                sendErrorNotification(e.getMessage(), user);
            }
        }
    }
    
    public final void processError(ResponseEvent responseEvent) {
        Response response = responseEvent.getResponse();
        FromHeader fromHeader = (FromHeader) response.getHeader(FromHeader.NAME);
        final String userName = fromHeader.getAddress().getDisplayName();
        userService.remove(userName);
        LOG.debug("The client " + userName + " send back the status code (" + getTextStatusCode(response.getStatusCode())
                + ") and therefore gets removed from the list of active clients.");
    }

    private final String getTextStatusCode(int code) {
        if (SIP_MESSAGE_CODES.containsKey(code)) {
            return SIP_MESSAGE_CODES.get(code);
        }
        return "Invalid code" + code;
    }
    
    private final void sendErrorNotification(final String message, SipUser user){
        try {
            ErrorNotificationProcessor errorProcessor 
                    = new ErrorNotificationProcessor(sipPresenceAgent.getConfiguration(), sipPresenceAgent.getProvider(), user);
            errorProcessor.process(message);
            user.getDialog().delete();
        } catch (ParseException | SipException ex) {
            LOG.error("Exception thrown during Error processing in the ResponseMessageProcessor.", ex);
        }
    }
}
