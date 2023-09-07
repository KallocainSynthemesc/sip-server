package com.avinvivo.sip.server.processor.subscribe;

import com.avinvivo.sip.server.bean.Configuration;
import com.avinvivo.sip.server.bean.SipSubject;
import com.avinvivo.sip.server.bean.SipUser;
import com.avinvivo.sip.server.dao.ConfigurationDao;
import com.avinvivo.sip.server.exception.AuthException;
import java.text.ParseException;

import javax.sip.Dialog;
import javax.sip.InvalidArgumentException;
import javax.sip.SipException;
import javax.sip.SipProvider;
import javax.sip.message.Response;

import org.apache.camel.component.sip.SipConfiguration;

import gov.nist.javax.sip.message.SIPRequest;
import static javax.sip.header.SubscriptionStateHeader.PENDING;
import static com.avinvivo.sip.server.security.AuthValidator.getSipUserFromTokenVerification;
import com.avinvivo.sip.server.service.SipUserService;
import com.avinvivo.sip.server.processor.response.ResponseConsumer;

public class SubscribeInitialiser extends AbstractSubscribeProcessor {

    private final SipUserService userService = SipUserService.getInstance();
    private final ConfigurationDao dao = ConfigurationDao.getInstance();
    
    public SubscribeInitialiser(SipConfiguration configuration, SipProvider provider,
            SIPRequest request, ResponseConsumer<Response> sendConsumer) throws ParseException {
        super(configuration, provider, request, sendConsumer);
    }

    @Override
    public void process(Dialog dialog)
            throws ParseException, SipException, InvalidArgumentException {
        try {
            final SipUser userdata = getSipUserFromTokenVerification(getRequest());
            //validateAuthentication(getSipMessageUser(), userdata);
            userdata.setDialog(dialog);
            userdata.setSipadresse(getRequest().getFrom().getAddress().getURI().toString());
            userdata.setSubsriptionState(PENDING);
	    if (userService.isSipAddresseUserUnique(userdata)) {
		userService.save(userdata);
		getResponder().configureInitialSubscribeResponse(dialog);
		SipSubject subject = new SipSubject(SipSubject.SipSubjectType.SUBSCRIBE_INITIALIZE, null);
		sendSubscriptionStateNotification(getResponder(),
			dialog, PENDING, subject,
			getConfiguration().getContactHeader(),
			getConfiguration().getExpiresHeader());
	    }
	    else
	    {
		throw new AuthException(AuthException.ErrorCode.USERNAME_SIP_ADDRESS_IN_USE);
	    }
        } catch (AuthException ex) {
            Configuration endpoint = this.dao.selectByName(ConfigurationDao.OAUTH_RECEIVE_TOKEN_ENDPOINT);
            getResponder().sendUnauthorized(ex.getMessage(),endpoint.getValue());
        }
    }
}
