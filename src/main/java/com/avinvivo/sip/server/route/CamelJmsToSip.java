/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.avinvivo.sip.server.route;

import com.avinvivo.sip.server.bean.Configuration;
import com.avinvivo.sip.server.dao.ConfigurationDao;
import com.avinvivo.sip.server.dao.datainitialization.ConfigurationDataInitService;

import org.apache.camel.CamelContext;
import org.apache.camel.component.jms.JmsComponent;
import static org.apache.camel.component.jms.JmsComponent.jmsComponentAutoAcknowledge;
import org.apache.camel.impl.DefaultCamelContext;

/**
 * An example class for demonstrating some basics behind Camel. This example
 * sends some text messages on to a JMS Queue, consumes them and persists them
 * to disk
 */
public final class CamelJmsToSip {

	private static final CamelContext context = new DefaultCamelContext();

	/*In case I need this properties I put them here because I can collapse them and I don't have to see them.
	Properties props = new Properties();
	props.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.enterprise.naming.SerialInitContextFactory");
	props.put(Context.URL_PKG_PREFIXES, "com.sun.enterprise.naming");
	props.put(Context.STATE_FACTORIES, "com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl");
	props.put(ORBLocator.OMG_ORB_INIT_HOST_PROPERTY, ORBLocator.DEFAULT_ORB_INIT_HOST); // localhost
	props.put(ORBLocator.OMG_ORB_INIT_PORT_PROPERTY, ORBLocator.DEFAULT_ORB_INIT_PORT); // 3700
	 */
	public static void main(String[] args) throws Exception {

		ConfigurationDataInitService service = new ConfigurationDataInitService();
		service.load();
		Configuration password = ConfigurationDao.getInstance().selectByName(ConfigurationDao.OAUTH_CLIENT_PASSWORD);
		Configuration clientId = ConfigurationDao.getInstance().selectByName(ConfigurationDao.OAUTH_CLIENT_NAME);

		com.sun.messaging.ConnectionFactory concreteFactory = new com.sun.messaging.ConnectionFactory();
		JmsComponent jmsComponent = jmsComponentAutoAcknowledge(concreteFactory);
		jmsComponent.getConfiguration().setUsername(clientId.getValue());
		jmsComponent.getConfiguration().setPassword(password.getValue());
		context.addComponent("jmsComponent", jmsComponent);
		context.addRoutes(new CustomRouteBuilder(context));
		context.start();
	}

	public static void closeCamelContext() throws Exception {
		context.stop();
	}
}
