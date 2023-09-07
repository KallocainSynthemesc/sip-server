package com.avinvivo.sip.server.processor.publish;

import com.avinvivo.sip.server.processor.request.Requester;

import javax.sip.SipProvider;

import org.apache.camel.component.sip.SipConfiguration;

public abstract class AbstractNotificationProcessor {

    private final SipConfiguration configuration;
    private final Requester requester;

    public AbstractNotificationProcessor(final SipConfiguration configuration, SipProvider provider){
        this.configuration = configuration;
        this.requester = new Requester(configuration, provider);
    }

    SipConfiguration getConfiguration() {
        return configuration;
    }

    Requester getRequester() {
        return requester;
    }

}
