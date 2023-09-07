package com.avinvivo.sip.server.processor.publish;

import com.avinvivo.sip.server.bean.SipContentBody;
import com.avinvivo.sip.server.bean.SipSubject;
import com.avinvivo.sip.server.bean.SipSubject.SipSubjectType;
import com.avinvivo.sip.server.bean.SipUser;
import com.avinvivo.sip.server.service.SipContentBodyService;
import static com.avinvivo.sip.server.service.SipContentBodyService.getInstance;
import java.text.ParseException;
import java.util.List;
import javax.sip.SipException;

import javax.sip.SipProvider;
import javax.sip.header.EventHeader;
import javax.sip.header.SubjectHeader;
import javax.sip.header.SubscriptionStateHeader;
import static javax.sip.header.SubscriptionStateHeader.ACTIVE;
import javax.sip.message.Request;

import org.apache.camel.component.sip.SipConfiguration;

public class PendingNotificationProcessor extends AbstractNotificationProcessor {

    private final SipContentBodyService messageService = getInstance();
    private final SipUser user;

    public PendingNotificationProcessor(final SipConfiguration configuration, SipProvider provider, SipUser user){
        super(configuration, provider);
        this.user = user;
    }

    public final void process() throws ParseException, SipException{
        SubscriptionStateHeader sstate = getRequester().createSubscriptionStateHeader(ACTIVE);
        List<SipContentBody> messages = this.messageService.selectAllByClient(user.getUser_name());
        for (SipContentBody message : messages) {
            EventHeader evtHeader = getConfiguration().getHeaderFactory().createEventHeader(message.getEventName());
            evtHeader.setEventId(message.getEventId());
            
            SubjectHeader subjHeader = getConfiguration().getHeaderFactory()
                    .createSubjectHeader(new SipSubject(SipSubjectType.MESSAGE_ID, message.getId()).toString());
                        
            Request request = getRequester().createNotifyRequest(user.getDialog(), message.getMessage(),
                    sstate, evtHeader, subjHeader);
            getRequester().sendNotification(request, user.getDialog());
        }
    }
}
