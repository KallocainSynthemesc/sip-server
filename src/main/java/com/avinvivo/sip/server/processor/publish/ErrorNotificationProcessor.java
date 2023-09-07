/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avinvivo.sip.server.processor.publish;

import com.avinvivo.sip.server.bean.SipSubject;
import com.avinvivo.sip.server.bean.SipUser;
import com.avinvivo.sip.server.bean.SipSubject.SipSubjectType;
import java.text.ParseException;
import javax.sip.SipException;
import javax.sip.SipProvider;
import javax.sip.header.EventHeader;
import javax.sip.header.SubjectHeader;
import javax.sip.header.SubscriptionStateHeader;
import static javax.sip.header.SubscriptionStateHeader.TERMINATED;
import javax.sip.message.Request;
import org.apache.camel.component.sip.SipConfiguration;

/**
 *
 * @author Kilian
 */
public class ErrorNotificationProcessor extends AbstractNotificationProcessor{
    
    private final SipUser user;
    
    public ErrorNotificationProcessor(SipConfiguration configuration, SipProvider provider, SipUser user) {
        super(configuration, provider);
        this.user = user;
    }
    
    public final void process(String message) throws ParseException, SipException {
        SubscriptionStateHeader sstate = getRequester().createSubscriptionStateHeader(TERMINATED);
        EventHeader evtHeader = getConfiguration().getHeaderFactory().createEventHeader("ERROR");
            //evtHeader.setEventId("ERRORMESSAGE");
        SubjectHeader subjHeader = getConfiguration().getHeaderFactory()
                .createSubjectHeader(new SipSubject(SipSubjectType.MISSING_SUBJECT, null).toString());
        Request request = getRequester().createNotifyRequest(user.getDialog(), message,
                sstate, evtHeader, subjHeader);
        getRequester().sendNotification(request, user.getDialog());

    }
}
