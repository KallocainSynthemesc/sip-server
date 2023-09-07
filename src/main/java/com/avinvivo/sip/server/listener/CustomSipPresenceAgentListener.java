package com.avinvivo.sip.server.listener;

import com.avinvivo.sip.server.processor.RequestMessageProcessor;
import com.avinvivo.sip.server.processor.ResponseMessageProcessor;
import com.avinvivo.sip.server.processor.enums.SipRequestType;
import static com.avinvivo.sip.server.processor.enums.SipRequestType.valueOf;
import com.avinvivo.sip.server.processor.enums.SipResponseType;
import static com.avinvivo.sip.server.processor.enums.SipResponseType.fromCode;
import javax.sip.DialogTerminatedEvent;
import javax.sip.IOExceptionEvent;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.TransactionTerminatedEvent;
import javax.sip.message.Request;
import javax.sip.message.Response;

import org.apache.camel.component.sip.listener.SipMessageCodes;
import org.slf4j.Logger;

import gov.nist.javax.sip.DialogTimeoutEvent;
import gov.nist.javax.sip.SipListenerExt;
import static org.slf4j.LoggerFactory.getLogger;

/*So theoretically this class gets called in a multithreaded environment. However objects
 *used by the original (and therefore our) SipPresenceAgentListener are not made for that.
 *I think we should be safe as long as we only read from those mutable references and hope no other
 *thread would dare to modify them after creation. If we ever need to change the state of 
 *SipPresenceAgent internal data in this listener we need to do so with great care.*/
public final class CustomSipPresenceAgentListener implements SipListenerExt, SipMessageCodes {

    private static final Logger LOG = getLogger(CustomSipPresenceAgentListener.class);
    private final RequestMessageProcessor requestService;
    private final ResponseMessageProcessor responseService;

    public CustomSipPresenceAgentListener(CustomSipPresenceAgent sipPresenceAgent) {
        this.requestService = new RequestMessageProcessor(sipPresenceAgent);
        this.responseService = new ResponseMessageProcessor(sipPresenceAgent);
    }

    @Override
    public final void processRequest(RequestEvent requestEvent) {
        final Request request = requestEvent.getRequest();
        SipRequestType type = valueOf(request.getMethod());
        requestService.computeRequest(type, requestEvent);
    }

    //weird synchronized tag here from original author. Don't think its necessary. 
    @Override
    public final synchronized void processResponse(ResponseEvent responseReceivedEvent) {
        Response response = responseReceivedEvent.getResponse();
        Integer statusCode = response.getStatusCode();
        SipResponseType type = fromCode(statusCode);
        responseService.computeResponse(type, responseReceivedEvent);
    }

    @Override
    public final void processTimeout(javax.sip.TimeoutEvent timeoutEvent) {
        if (LOG.isWarnEnabled()) {
            LOG.warn("TimeoutEvent received at Sip Presence Agent Listener");
        }
    }

    @Override
    public final void processIOException(IOExceptionEvent exceptionEvent) {
        if (LOG.isWarnEnabled()) {
            LOG.warn("IOExceptionEvent received at SipPresenceAgentListener");
        }
    }

    @Override
    public final void processTransactionTerminated(TransactionTerminatedEvent transactionTerminatedEvent) {
        if (LOG.isWarnEnabled()) {
            LOG.warn("TransactionTerminatedEvent received at SipPresenceAgentListener");
        }
    }

    @Override
    public final void processDialogTerminated(DialogTerminatedEvent dialogTerminatedEvent) {
        if (LOG.isWarnEnabled()) {
            LOG.warn("DialogTerminatedEvent received at SipPresenceAgentListener");
        }
    }

    @Override
    public final void processDialogTimeout(DialogTimeoutEvent timeoutEvent) {
        LOG.warn("processDialogTimeout: \n{}", timeoutEvent);
    }
}
