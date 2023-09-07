package com.avinvivo.sip.server.processor.request;

import java.text.ParseException;

import javax.sip.ClientTransaction;
import javax.sip.Dialog;
import javax.sip.SipException;
import javax.sip.SipProvider;
import javax.sip.address.SipURI;
import javax.sip.header.Header;
import javax.sip.header.SubscriptionStateHeader;
import javax.sip.message.Request;
import static javax.sip.message.Request.NOTIFY;

import org.apache.camel.component.sip.SipConfiguration;

public class Requester {

    private final SipConfiguration configuration;
    private final SipProvider provider;

    public Requester(SipConfiguration configuration, SipProvider provider) {
        this.configuration = configuration;
        this.provider = provider;
    }

    public void sendNotification(Request notifyRequest, Dialog clientDialog) throws SipException, ParseException {
        ClientTransaction clientTransactionId = provider.getNewClientTransaction(notifyRequest);
        clientDialog.sendRequest(clientTransactionId);
    }

    public SubscriptionStateHeader createSubscriptionStateHeader(final String state) throws ParseException {
        /*
		 * NOTIFY requests MUST contain a "Subscription-State" header with a value of
		 * "active", "pending", or "terminated". The "active" value indicates that the
		 * subscription has been accepted and has been authorized (in most cases; see
		 * section 5.2.). The "pending" value indicates that the subscription has been
		 * received, but that policy information is insufficient to accept or deny the
		 * subscription at this time. The "terminated" value indicates that the
		 * subscription is not active.
         */
        return configuration.getHeaderFactory().createSubscriptionStateHeader(state);
    }

    public Request createNotifyRequest(Dialog clientDialog, String message, Header... headers)
            throws ParseException, SipException {
        final Request notifyRequest = clientDialog.createRequest(NOTIFY);

        // Mark the contact header, to check that the remote contact is updated
        ((SipURI) configuration.getContactHeader().getAddress().getURI()).setParameter(
                configuration.getFromUser(), configuration.getFromHost());
        notifyRequest.setHeader(configuration.getContactHeader());

        for (Header header : headers) {
            notifyRequest.addHeader(header);
        }
        notifyRequest.setContent(message, configuration.getContentTypeHeader());

        return notifyRequest;
    }
}
