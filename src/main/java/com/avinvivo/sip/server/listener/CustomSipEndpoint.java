package com.avinvivo.sip.server.listener;

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.component.sip.SipConfiguration;
import org.apache.camel.component.sip.SipEndpoint;
import org.apache.camel.component.sip.SipSubscriber;

public class CustomSipEndpoint extends SipEndpoint{

	public CustomSipEndpoint(String endpointUri, Component component, SipConfiguration configuration) {
		super(endpointUri, component, configuration);
	}

	@Override
	public Consumer createConsumer(Processor processor) throws Exception {
        if (getConfiguration().isPresenceAgent()) {
            final CustomSipPresenceAgent answer = new CustomSipPresenceAgent(this, processor, getConfiguration());
            configureConsumer(answer);
            return answer;
        } else {
            final SipSubscriber answer = new SipSubscriber(this, processor, getConfiguration());
            configureConsumer(answer);
            return answer;
            //throw new ConfigurationException("Configuring a Subscriber route is forbidden. Please specify a PresenceAgent.");
        }
    }
}
