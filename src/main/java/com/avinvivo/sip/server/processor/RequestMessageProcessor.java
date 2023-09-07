package com.avinvivo.sip.server.processor;

import com.avinvivo.sip.server.processor.enums.SipRequestType;
import com.avinvivo.sip.server.listener.CustomSipPresenceAgent;
import com.avinvivo.sip.server.processor.publish.PublishNotificationProcessor;
import com.avinvivo.sip.server.processor.subscribe.AbstractSubscribeProcessor;
import com.avinvivo.sip.server.processor.subscribe.SubscribeInitialiser;
import com.avinvivo.sip.server.processor.subscribe.SubscribeRefresher;
import com.avinvivo.sip.server.processor.subscribe.SubscribeTerminator;
import javax.sip.Dialog;
import javax.sip.RequestEvent;
import javax.sip.ServerTransaction;
import javax.sip.SipProvider;
import javax.sip.TransactionAlreadyExistsException;
import javax.sip.TransactionUnavailableException;

import org.apache.camel.component.sip.SipConfiguration;
import org.slf4j.Logger;

import gov.nist.javax.sip.message.SIPRequest;
import static org.slf4j.LoggerFactory.getLogger;

public final class RequestMessageProcessor {

    private static final Logger LOG = getLogger(RequestMessageProcessor.class);
    private final CustomSipPresenceAgent sipPresenceAgent;

    public RequestMessageProcessor(final CustomSipPresenceAgent sipPresenceAgent) {
        this.sipPresenceAgent = sipPresenceAgent;
    }

    public void computeRequest(SipRequestType type, RequestEvent requestEvent) {
        type.compute.accept(this, requestEvent);
    }

    public final void processPublish(RequestEvent requestEvent) {
        try {
            final SIPRequest request = (SIPRequest) requestEvent.getRequest();
            final PublishNotificationProcessor publishProcessor
                    = new PublishNotificationProcessor(getConfig(), getProvider(), request);
            publishProcessor.process();
        } catch (Exception e) {
            LOG.error("Exception thrown during publish/notify processing in the RequestMessageProcessor", e);
        }
    }

    public void processSubscribe(RequestEvent requestEvent) {
        SIPRequest request = (SIPRequest) requestEvent.getRequest();
        try {
            final ServerTransaction st
                    = getOrCreateServerTrasaction(requestEvent, getProvider());
            final Dialog dialog = getOrCreateDialog(st, requestEvent);
            final AbstractSubscribeProcessor subscribeProcessor;
            if (dialog.getState() == null) {
                subscribeProcessor = new SubscribeInitialiser(getConfig(), getProvider(),
                        request, st::sendResponse);
            } else if (request.getExpires().getExpires() <= 0) {
                subscribeProcessor = new SubscribeTerminator(getConfig(), getProvider(),
                        request, st::sendResponse);
            } else {
                subscribeProcessor = new SubscribeRefresher(getConfig(), getProvider(),
                        request, st::sendResponse);
            }
            subscribeProcessor.process(dialog);
        } catch (Throwable e) {
            LOG.error("Exception thrown during Notify "
                    + "processing in the RequestMessageProcessor.", e);
	    System.out.println("Exception thrown during Notify "
                    + "processing in the RequestMessageProcessor: "+ e.getMessage());
        }
    }

    private final Dialog getOrCreateDialog(ServerTransaction st, RequestEvent requestEvent) {
        Dialog dialog = requestEvent.getDialog();
        if (dialog == null) {
            dialog = st.getDialog();
        }
        return dialog;
    }

    private final ServerTransaction getOrCreateServerTrasaction(RequestEvent requestEvent, SipProvider sipProvider)
            throws TransactionAlreadyExistsException, TransactionUnavailableException {
        ServerTransaction st = requestEvent.getServerTransaction();
        if (st == null) {
            st = sipProvider.getNewServerTransaction(requestEvent.getRequest());
        }
        return st;
    }

    private final SipConfiguration getConfig() {
        return this.sipPresenceAgent.getConfiguration();
    }

    private final SipProvider getProvider() {
        return this.sipPresenceAgent.getProvider();
    }
}
