/*******************************************************************************
 * Copyright 2012 Amit Portnoy
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package graphpack;

import graphpack.parsing.scala.Parser;
import graphpack.remote.rmi.ConnectionManager;
import graphpack.remote.rmi.IProxyClientFactory;
import graphpack.remote.rmi.IProxyNodeFactory;
import graphpack.remote.rmi.IProxyServiceFactory;
import graphpack.remote.rmi.ProxyClient;
import graphpack.remote.rmi.ProxyNode;
import graphpack.remote.rmi.ProxyService;

import java.rmi.registry.Registry;

import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 * a concrete {@link GraphPackClient} that is implemented using: <br/>
 * connection = {@link graphpack.remote.rmi.ConnectionManager} <br/>
 * @author amitport
 */
public class RmiGraphPackClient extends GraphPackClient {
	Registry registry;
	public RmiGraphPackClient(Registry registry) {
		super(ConnectionManager.class,Parser.class);
		this.registry = registry;
		init();
	}
	
	@Override 
	protected void configure() {
		super.configure();
		
		bind(Registry.class).toInstance(registry);
		
		install(new FactoryModuleBuilder()
		.implement(ProxyService.class,ProxyService.class)
		.build(IProxyServiceFactory.class));
		install(new FactoryModuleBuilder()
		.implement(ProxyClient.class,ProxyClient.class)
		.build(IProxyClientFactory.class));
		install(new FactoryModuleBuilder()
		.implement(ProxyNode.class,ProxyNode.class)
		.build(IProxyNodeFactory.class));
	}
}
