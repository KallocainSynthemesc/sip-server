/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avinvivo.sip.server.security;

import com.avinvivo.sip.server.bean.SipUser;
import com.avinvivo.sip.server.exception.AuthException;
import com.avinvivo.sip.server.utils.SipUtils;
import gov.nist.javax.sip.header.Authorization;
import gov.nist.javax.sip.message.SIPMessage;
import java.text.ParseException;
import java.util.Iterator;
import javax.sip.InvalidArgumentException;
import javax.sip.address.SipURI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Kilian
 */
public class AuthValidatorTest {

    public AuthValidatorTest() {
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of getSipUserFromTokenVerification method, of class AuthValidator.
     */
    @Test
    public void testGetSipUserFromTokenVerificationNoToken() throws Exception {
        System.out.println("testGetSipUserFromTokenVerificationNoToken");
        Authorization auth = new Authorization();
        auth.setParameter(AuthValidator.TOKEN_TYPE, null);
        SIPMessageMock message = new SIPMessageMock();
        message.setAuthorization(auth);
        AuthException thrown = Assertions.assertThrows(AuthException.class, () -> {
            SipUser result = AuthValidator.getSipUserFromTokenVerification(message);
        });
        AuthException ex = new AuthException(AuthException.ErrorCode.MISSING_TOKEN);
        Assertions.assertEquals(ex.getMessage(), thrown.getMessage());
    }
    
    @Test
    public void testGetSipUserFromTokenVerificationNoAuthHeader() throws Exception {
        System.out.println("testGetSipUserFromTokenVerificationNoAuthHeader");
        SIPMessageMock message = new SIPMessageMock();
        AuthException thrown = Assertions.assertThrows(AuthException.class, () -> {
            SipUser result = AuthValidator.getSipUserFromTokenVerification(message);
        });
        AuthException ex = new AuthException(AuthException.ErrorCode.MISSING_AUTH_HEADER_ERROR);
        Assertions.assertEquals(ex.getMessage(), thrown.getMessage());
    }
    
    @Test
    public void testValidateAuthenticationInactive() throws Exception {
        System.out.println("testValidateAuthenticationInactive");
        SipUser userdata = new SipUser();
        userdata.setUser_name("ced");
        userdata.setActive(false);
        AuthException thrown = Assertions.assertThrows(AuthException.class, () -> {
            AuthValidator.checkUserActive(userdata);
        });
        AuthException ex = new AuthException(AuthException.ErrorCode.VERIFIED_USER_INACTIVE);
        Assertions.assertEquals(ex.getMessage(), thrown.getMessage());
    }
    
    class SIPMessageMock extends SIPMessage {

        private Authorization auth = null;

        @Override
        public StringBuilder encodeMessage(StringBuilder retval) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getFirstLine() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void setSIPVersion(String sipVersion) throws ParseException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getSIPVersion() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String toString() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Authorization getAuthorization() {
            return this.auth;
        }

        public void setAuthorization(Authorization auth) {
            this.auth = auth;
        }

    }
    
    class SipURImock implements SipURI{

        String user = null;
        String host = null;
        @Override
        public void setUser(String string) throws ParseException {
            this.user = string;
        }

        @Override
        public String getUser() {
            return this.user;
        }

        @Override
        public void setUserPassword(String string) throws ParseException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getUserPassword() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean isSecure() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void setSecure(boolean bln) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void setHost(String string) throws ParseException {
            this.host = string;
        }

        @Override
        public String getHost() {
            return this.host;
        }

        @Override
        public void setPort(int i) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int getPort() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void removePort() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getHeader(String string) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void setHeader(String string, String string1) throws ParseException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Iterator getHeaderNames() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getTransportParam() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void setTransportParam(String string) throws ParseException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int getTTLParam() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void setTTLParam(int i) throws InvalidArgumentException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getMethodParam() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void setMethodParam(String string) throws ParseException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void setUserParam(String string) throws ParseException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getUserParam() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getMAddrParam() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void setMAddrParam(String string) throws ParseException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean hasLrParam() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void setLrParam() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getScheme() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean isSipURI() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getParameter(String string) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void setParameter(String string, String string1) throws ParseException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Iterator getParameterNames() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void removeParameter(String string) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
        @Override
        public Object clone(){
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
    }
}
