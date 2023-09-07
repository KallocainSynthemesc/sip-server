package com.avinvivo.sip.server.route;

import com.avinvivo.bimi.notification.Notification;
import static com.avinvivo.sip.server.utils.SipUtils.PUBLISH_USER;
import static com.avinvivo.sip.server.utils.SipUtils.getLocalId;
import static com.avinvivo.sip.server.utils.SipUtils.serializeNotification;
import static java.net.URI.create;
import java.net.UnknownHostException;
import static java.util.Collections.emptyMap;

import static javax.sip.message.Request.PUBLISH;
import com.avinvivo.sip.server.listener.CustomSipEndpoint;
import fr.c2lr.j2eeBase.J2eeBaseUtils;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.sip.SipComponent;
import org.apache.camel.component.sip.SipConfiguration;
import org.apache.log4j.Logger;

class CustomRouteBuilder extends RouteBuilder {

	private final static Logger LOGGER = Logger.getLogger(CustomRouteBuilder.class);

	private final String localIP;
	private final String agentURI;
	/*
	 * We get an Object from the JMS message. Thats cool. However
	 * this object in the body gets again parsed by the SIP PipelinedMsgParser.
	 * It transforms a Message (as Object) to a byte[] representation. We can't use an ObjectInputStream
	 * with this parser (We have a parser differential) and can't call an unparse function on the PipelinedMsgParser.
	 * However if the whole thing is a byte[] already, the parser is not doing any modification.
	 * We can therefore later in code get our Object back with ObjectInputSream.
	 */
	Processor processor = (Exchange exchange) -> {
		//We could save the message here, but I rather save the message once
		//the publish user is authenticated and everything.
		Message message = exchange.getIn();
		Notification notification = (Notification) message.getBody();
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace(J2eeBaseUtils.ANSI_COLOR.WHITE + ">> Receive notification from JMS : " + notification + "\n");
		}
		byte[] data = serializeNotification(notification);
		exchange.getIn().setBody(data);
	};

	private CamelContext context;

	public CustomRouteBuilder(CamelContext context) throws UnknownHostException {
		this.context = context;
		this.localIP = getLocalId();
		this.agentURI = "sip://agent@" + this.localIP + ":5252";
	}

	private Endpoint initFromEndpoint(){
		SipConfiguration configuration = new SipConfiguration();
		final String endpointUri = this.agentURI
				+ "?stackName=PresenceAgent"
				+ "&presenceAgent=true"
				+ "&eventHeaderName=evtHdrName"
				+ "&eventId=evtid"
				+ "&useRouterForAllUris=true";
		SipComponent sipComponent = new SipComponent();
		sipComponent.setCamelContext(context);
		configuration.initialize(create(endpointUri), emptyMap(), sipComponent);
		configuration.setPresenceAgent(true);
		configuration.setEventHeaderName("evtHdrName");
		configuration.setEventId("evtid");
		configuration.setStackName("PresenceAgent");
		configuration.setTransport("tcp");

		CustomSipEndpoint endpointImpl = new CustomSipEndpoint(endpointUri, new SipComponent(), configuration);
		return endpointImpl;
	}

	@Override
	public void configure(){

		from(initFromEndpoint()).to("mock:neverland");

		from("jmsComponent:queue:test").process(processor)
				.setHeader("REQUEST_METHOD", constant(PUBLISH))
				.to(this.agentURI + "?stackName=client&eventHeaderName=evtHdrName&eventId=evtid&fromUser="
						+ PUBLISH_USER + "&fromHost="+this.localIP+"&fromPort=3534");
	}
}