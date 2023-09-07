package com.avinvivo.sip.server.listener;

import static java.lang.Integer.valueOf;
import java.util.Properties;

import javax.sip.SipProvider;
import javax.sip.SipStack;

import org.apache.camel.Processor;
import org.apache.camel.component.sip.SipConfiguration;
import org.apache.camel.component.sip.SipEndpoint;
import org.apache.camel.impl.DefaultConsumer;

public final class CustomSipPresenceAgent extends DefaultConsumer{
	
    private final SipConfiguration configuration;
    private CustomSipPresenceAgentListener sipPresenceAgentListener;
    private SipProvider provider; 
    private SipStack sipStack;
    
	public CustomSipPresenceAgent(final SipEndpoint sipEndpoint, final Processor processor, final SipConfiguration configuration) {
        super(sipEndpoint, processor);
        this.configuration = configuration;
	}

    @Override
    protected final void doStart() throws Exception {
        super.doStart();
        this.configuration.setConsumer(true);
        final Properties properties = customCreateInitialProperties();
        this.sipStack = configuration.getSipFactory().createSipStack(properties);
        
        configuration.parseURI();
        this.sipPresenceAgentListener = new CustomSipPresenceAgentListener(this);
        configuration.setListeningPoint(sipStack.createListeningPoint(configuration.getFromHost(), 
                    valueOf(configuration.getFromPort()), 
                    configuration.getTransport()));
        provider = getSipStack().createSipProvider(configuration.getListeningPoint());
        provider.addSipListener(sipPresenceAgentListener);
    }

    @Override
    protected final void doStop() throws Exception {
        super.doStop(); 
        getSipStack().deleteListeningPoint(configuration.getListeningPoint());
        provider.removeSipListener(sipPresenceAgentListener);
        getSipStack().deleteSipProvider(provider);
        getSipStack().stop();
    }

	public final SipConfiguration getConfiguration() {
        return configuration;
    }

	public final SipProvider getProvider() {
        return provider;
    }

	public final SipStack getSipStack() {
        return sipStack;
    }
    
    final Properties customCreateInitialProperties() {
        final Properties properties = new Properties();
        properties.setProperty("javax.sip.STACK_NAME", getConfiguration().getStackName());
        properties.setProperty("gov.nist.javax.sip.MAX_MESSAGE_SIZE", "" + getConfiguration().getMaxMessageSize());
        properties.setProperty("gov.nist.javax.sip.CACHE_CLIENT_CONNECTIONS", "" + getConfiguration().isCacheConnections());
        properties.setProperty("javax.sip.USE_ROUTER_FOR_ALL_URIS", "" + getConfiguration().isUseRouterForAllUris());
        //gov.nist.javax.sip.MESSAGE_PARSER_FACTORY could be interesting
        //gov.nist.javax.sip.MIN_KEEPALIVE_TIME_SECONDS
        
        if ((getConfiguration().getImplementationDebugLogFile() != null) && (getConfiguration().getImplementationServerLogFile() != null)) {
            properties.setProperty("gov.nist.javax.sip.DEBUG_LOG", getConfiguration().getImplementationDebugLogFile());
            properties.setProperty("gov.nist.javax.sip.SERVER_LOG", getConfiguration().getImplementationServerLogFile());
            properties.setProperty("gov.nist.javax.sip.TRACE_LEVEL", getConfiguration().getImplementationTraceLevel());
        }
        
        return properties;
    }
}
