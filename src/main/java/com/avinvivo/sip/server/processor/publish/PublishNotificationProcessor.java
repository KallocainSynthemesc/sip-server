package com.avinvivo.sip.server.processor.publish;

import com.avinvivo.bimi.notification.Notification;
import com.avinvivo.sip.server.bean.SipContentBody;
import static com.avinvivo.sip.server.bean.SipContentBody.newInstance;
import com.avinvivo.sip.server.bean.SipSubject;
import com.avinvivo.sip.server.bean.SipUser;
import com.avinvivo.sip.server.exception.AuthException;
import com.avinvivo.sip.server.exception.InvalidSipMessageException;
import com.avinvivo.sip.server.exception.InvalidSipMessageException.ErrorCode;
import com.avinvivo.sip.server.service.SipContentBodyService;
import com.avinvivo.sip.server.processor.response.Responder;
import static com.avinvivo.sip.server.security.AuthValidator.verifyPublishUser;
import com.avinvivo.sip.server.service.SipUserService;
import static com.avinvivo.sip.server.utils.SipUtils.deserializeToNotification;
import gov.nist.javax.sip.message.SIPMessage;
import java.text.ParseException;
import java.util.List;

import javax.sip.SipProvider;
import javax.sip.header.EventHeader;
import javax.sip.header.SubjectHeader;
import javax.sip.header.SubscriptionStateHeader;
import javax.sip.message.Request;

import org.apache.camel.component.sip.SipConfiguration;

import gov.nist.javax.sip.message.SIPRequest;
import javax.sip.address.SipURI;
import static javax.sip.header.EventHeader.NAME;
import javax.sip.header.FromHeader;
import static javax.sip.header.SubscriptionStateHeader.ACTIVE;

public class PublishNotificationProcessor extends AbstractNotificationProcessor{
	
	private final SIPRequest request;

	private final Responder responder;
	
	private final SipContentBodyService messageService = SipContentBodyService.getInstance();
        
        private final SipUserService userService = SipUserService.getInstance();
	
	public PublishNotificationProcessor(final SipConfiguration configuration, SipProvider provider, 
			SIPRequest request) throws ParseException {
		super(configuration, provider);
		this.request = request;
		this.responder = new Responder(configuration, request, provider::sendResponse);
	}
	
    public final void process() throws Exception {
        try {
            verifyPublishUser(getUriFromHeader(request));
            EventHeader eventHeader = (EventHeader) request.getHeader(NAME);
            sendPublishNotifications(deserializeToNotification(request.getRawContent()), eventHeader);
            responder.sendAccepted();
        } catch (AuthException ex) {
            responder.sendUnauthorized(ex.getMessage());
        } catch(InvalidSipMessageException ex){
            responder.sendBadRequest(ex.getMessage());
        }
    }
    
    private final SipURI getUriFromHeader(SIPMessage sipMessage){
        final FromHeader fromHeader = sipMessage.getFromHeader();
        final SipURI uri = (SipURI) fromHeader.getAddress().getURI();
        return uri;
    }

    private final void sendPublishNotifications(Notification notification, EventHeader eventHeader)
            throws Exception {

        List<String> clients = notification.getClientsToNotify();
        final String message = notification.toString();
        if (message == null || message.isEmpty()) {
            throw new InvalidSipMessageException(ErrorCode.EMPTY_MESSAGE);
        }
        SubscriptionStateHeader sstate = getRequester().createSubscriptionStateHeader(ACTIVE);
        for (String client : clients) {
            if ("".equals(client) == false) {
                final SipContentBody sipMessage = newInstance(client, message, eventHeader.getEventType(),
                        eventHeader.getEventId());
                messageService.save(sipMessage);
                SipUser user = userService.get(client);
                if (user != null) {
                    SubjectHeader subjHeader = getConfiguration().getHeaderFactory()
                            .createSubjectHeader(new SipSubject(SipSubject.SipSubjectType.MESSAGE_ID, sipMessage.getId()).toString());
                    Request request = getRequester().createNotifyRequest(user.getDialog(),
                            sipMessage.getMessage(), sstate, eventHeader, subjHeader);
                    getRequester().sendNotification(request, user.getDialog());
                }
            }

        }
    }
}
