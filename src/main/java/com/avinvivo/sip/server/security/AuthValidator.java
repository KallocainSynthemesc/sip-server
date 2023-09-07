package com.avinvivo.sip.server.security;

import com.avinvivo.sip.server.bean.Configuration;
import com.avinvivo.sip.server.bean.SipUser;
import com.avinvivo.sip.server.dao.ConfigurationDao;
import com.avinvivo.sip.server.exception.AuthException;
import com.avinvivo.sip.server.exception.AuthException.ErrorCode;
import static com.avinvivo.sip.server.utils.SipUtils.isLocalAddress;
import javax.sip.address.SipURI;

import gov.nist.javax.sip.header.Authorization;
import gov.nist.javax.sip.message.SIPMessage;
import java.io.IOException;
import javax.ws.rs.BadRequestException;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;

public final class AuthValidator {
    
    //public final static String AUTH_URI = "http://localhost:8080/bimi-authorization/oauth/token";
    public final static String TOKEN_TYPE = "Bearer";
    private static final Logger LOG = getLogger(AuthValidator.class);
    
    public final static boolean verifyPublishUser(final SipURI uri) throws AuthException {
        Configuration publishUser = ConfigurationDao.getInstance().selectByName(ConfigurationDao.PUBLISH_USER);
        final boolean isKnownPublishUser = publishUser.getValue().equals(uri.getUser());
        if(isKnownPublishUser == false){
            throw new AuthException(ErrorCode.UNKOWN_PUBLISH_USER);
        }
        final boolean isLocalhostAddress = isLocalAddress(uri.getHost());
        if(isLocalhostAddress == false)
        {
            throw new AuthException(ErrorCode.PUBLISH_REQUEST_INVALID_HOST);
        }
        return isKnownPublishUser && isKnownPublishUser;
    }
    
    public final static SipUser getSipUserFromTokenVerification(SIPMessage sipMessage) throws AuthException {
        Authorization auth = sipMessage.getAuthorization();
        SipUser sipUser = new SipUser();
        if (auth != null) {
            final String tokenParam = auth.getParameter(TOKEN_TYPE);
            if (tokenParam != null) {
                RestClient client = new RestClient();
                try {
                    sipUser = client.verifyJsonToken(tokenParam);
                } catch (BadRequestException | IOException ex) {
                    LOG.error("Caught BadRequestException exception and throwing an AuthException", ex);
                    throw new AuthException(ErrorCode.TOKEN_VERIFICATION_FAILED, ex.getMessage());
                }
            } else {
                throw new AuthException(ErrorCode.MISSING_TOKEN);
            }
        } else {
            throw new AuthException(ErrorCode.MISSING_AUTH_HEADER_ERROR);
        }
        return sipUser;
    }

    public final static void checkUserActive(SipUser userdata) throws AuthException {
        if (userdata.isActive() == false) {
            throw new AuthException(ErrorCode.VERIFIED_USER_INACTIVE);
        }
    }
}
