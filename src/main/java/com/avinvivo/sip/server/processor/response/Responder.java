package com.avinvivo.sip.server.processor.response;

import java.text.ParseException;

import javax.sip.Dialog;
import javax.sip.InvalidArgumentException;
import javax.sip.SipException;
import javax.sip.header.Header;
import javax.sip.header.ToHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;

import org.apache.camel.component.sip.SipConfiguration;
import org.slf4j.Logger;

import static gov.nist.javax.sip.message.SIPResponse.getReasonPhrase;
import static java.util.UUID.randomUUID;
import static javax.sip.header.ToHeader.NAME;
import static javax.sip.message.Response.ACCEPTED;
import static javax.sip.message.Response.BAD_REQUEST;
import static javax.sip.message.Response.NOT_FOUND;
import static javax.sip.message.Response.UNAUTHORIZED;
import static org.slf4j.LoggerFactory.getLogger;

public final class Responder {

	private static final Logger LOG = getLogger(Responder.class);
	private final SipConfiguration configuration;
	private final Response response;
	private final ResponseConsumer<Response> sendResponse;

	public Responder(final SipConfiguration configuration, Request request,
			ResponseConsumer<Response> sendResponse) throws ParseException {
		LOG.debug("Responder: Received a " + request.getMethod() + " request");
		this.configuration = configuration;
		this.response = configuration.getMessageFactory().createResponse(NOT_FOUND,
				request);
		this.sendResponse = sendResponse;
	}

	public final void sendAccepted() throws ParseException, SipException, InvalidArgumentException {
		response.setStatusCode(ACCEPTED);
		response.setReasonPhrase(getReasonPhrase(ACCEPTED));
		sendResponse();
	}

	public final void addHeader(Header... headers) {
		for(Header header : headers)
		{
			this.response.addHeader(header);
		}
	}
        
        public final void sendUnauthorized(String reason, String content) throws ParseException, SipException, InvalidArgumentException {
		this.response.setStatusCode(UNAUTHORIZED);
                this.response.setReasonPhrase(reason);
		this.response.setContent(content, configuration.getContentTypeHeader());
		sendResponse();
	}
        
        public final void sendUnauthorized(String reason) throws ParseException, SipException, InvalidArgumentException {
		this.response.setStatusCode(UNAUTHORIZED);
                this.response.setReasonPhrase(reason);
		sendResponse();
	}
        
        public final void sendBadRequest(String reason) throws ParseException, SipException, InvalidArgumentException {
		this.response.setStatusCode(BAD_REQUEST);
                this.response.setReasonPhrase(reason);
		sendResponse();
	}

	private final void sendResponse() throws ParseException, SipException, InvalidArgumentException {
		LOG.debug("Responder sending response: {}", this.response);
		this.sendResponse.acceptThrows(this.response);
	}

	public final void configureInitialSubscribeResponse(Dialog dialog) throws ParseException, SipException {
		final String toTag = randomUUID().toString();
		final ToHeader toHeader = (ToHeader) response.getHeader(NAME);
		toHeader.setTag(toTag);
		dialog.terminateOnBye(false);
	}
}
