package com.avinvivo.sip.server.processor.enums;

import com.avinvivo.sip.server.processor.RequestMessageProcessor;
import java.util.function.BiConsumer;

import javax.sip.RequestEvent;

public enum SipRequestType {
	PUBLISH(RequestMessageProcessor::processPublish),
	SUBSCRIBE(RequestMessageProcessor::processSubscribe);

	public final BiConsumer<RequestMessageProcessor, RequestEvent> compute;

	private SipRequestType(BiConsumer<RequestMessageProcessor, RequestEvent> compute)
			throws ExceptionInInitializerError {
		this.compute = compute;
	}
}
