package com.avinvivo.sip.server.processor.subscribe;

import com.avinvivo.sip.server.bean.SipSubject;
import com.avinvivo.sip.server.processor.request.Requester;
import com.avinvivo.sip.server.processor.response.Responder;
import java.text.ParseException;

import javax.sip.Dialog;
import javax.sip.InvalidArgumentException;
import javax.sip.SipException;
import javax.sip.SipProvider;
import javax.sip.header.EventHeader;
import javax.sip.header.Header;
import javax.sip.header.SubscriptionStateHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;

import org.apache.camel.component.sip.SipConfiguration;

import gov.nist.javax.sip.message.SIPRequest;
import static javax.sip.header.EventHeader.NAME;
import com.avinvivo.sip.server.processor.response.ResponseConsumer;
import javax.sip.header.SubjectHeader;

public abstract class AbstractSubscribeProcessor {
	
	final private SipConfiguration configuration;

	final private SipProvider provider;
	
	final private SIPRequest request;

	final private Requester requester;

	final private Responder responder;
	
	public AbstractSubscribeProcessor(final SipConfiguration configuration, SipProvider provider, SIPRequest request,
			ResponseConsumer<Response> sendConsumer) throws ParseException {
		this.configuration = configuration;
		this.provider = provider;
		this.request = request;
		this.requester = new Requester(configuration, provider);
		this.responder = new Responder(configuration, request, sendConsumer);
	}
	
	public abstract void process(Dialog dialog)throws ParseException, SipException, InvalidArgumentException;

	protected final void sendSubscriptionStateNotification(final Responder responder, Dialog dialog,
			final String subscriptionStateHeader, SipSubject subject, Header... responseHeader)
			throws ParseException, SipException, InvalidArgumentException {
		responder.addHeader(responseHeader);
		responder.sendAccepted();
		SubscriptionStateHeader sstate = requester.createSubscriptionStateHeader(subscriptionStateHeader);
                SubjectHeader subjHeader = getConfiguration().getHeaderFactory()
                    .createSubjectHeader(subject.toString());
		final EventHeader eventHeader = (EventHeader) this.request.getHeader(NAME);
		final Request notifyRequest = requester.createNotifyRequest(dialog, "state-notify-message", sstate, eventHeader, subjHeader);
		
		requester.sendNotification(notifyRequest, dialog);
	}
	
	public final SipProvider getProvider() {
		return provider;
	}
	
	public final SipConfiguration getConfiguration() {
		return configuration;
	}
	
	public final SIPRequest getRequest() {
		return request;
	}

	public final Responder getResponder() {
		return responder;
	}
	
	public final Requester getRequester()
	{
		return requester;
	}
        
        public final String getSipMessageUser(){
            return getRequest().getFromHeader().getAddress().getDisplayName();
        }
}
