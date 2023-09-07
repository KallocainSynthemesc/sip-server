package com.avinvivo.sip.server.security;

import com.avinvivo.sip.server.bean.Configuration;
import com.avinvivo.sip.server.bean.SipUser;
import com.avinvivo.sip.server.dao.ConfigurationDao;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import org.codehaus.jackson.map.ObjectMapper;

public class RestClient {

	private final ConfigurationDao dao = ConfigurationDao.getInstance();

	public SipUser verifyJsonToken(final String token) throws IOException {
		final Configuration password = this.dao.selectByName(ConfigurationDao.OAUTH_CLIENT_PASSWORD);
		final Configuration clientId = this.dao.selectByName(ConfigurationDao.OAUTH_CLIENT_NAME);
		final Configuration urlStr = this.dao.selectByName(ConfigurationDao.OAUTH_TOKEN_ENDPOINT);

		final URL url = new URL(urlStr.getValue());
		final HttpPostMultipart multipart = new HttpPostMultipart(url, "utf-8", clientId.getValue(), password.getValue());
		final HashMap<String, String> data = new HashMap<>();
		data.put("token", token);
		multipart.addData(data);
		final String response = multipart.finish();
		final ObjectMapper objectMapper = new ObjectMapper();
		final SipUser tokenResponse = objectMapper.readValue(response, SipUser.class);

		return tokenResponse;

	}
}
