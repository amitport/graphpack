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

import graphpack.local.persistence.inmemory.ClientStore;
import graphpack.local.persistence.inmemory.EdgeStore;
import graphpack.local.persistence.inmemory.NodeStore;
import graphpack.parsing.java.IParser;
import graphpack.parsing.scala.Parser;
import graphpack.remote.rmi.ConnectionManager;
import graphpack.remote.rmi.IProxyClientFactory;
import graphpack.remote.rmi.IProxyNodeFactory;
import graphpack.remote.rmi.IProxyServiceFactory;
import graphpack.remote.rmi.ProxyClient;
import graphpack.remote.rmi.ProxyNode;
import graphpack.remote.rmi.ProxyService;
import graphpack.taskprocessing.quartz.TaskManager;

import java.rmi.registry.Registry;

import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 * a concrete {@link GraphPackService} that is implemented using: <br/>
 * persistence = {@link graphpack.local.persistence.inmemory.ClientStore}, {@link graphpack.local.persistence.inmemory.NodeStore}, {@link graphpack.local.persistence.inmemory.EdgeStore} <br/>
 * connection = {@link graphpack.remote.rmi.ConnectionManager} <br/>
 * task management = {@link graphpack.taskprocessing.quartz.TaskManager}
 * @author amitport
 */
public class RmiInmemoryQuartzGraphPackService extends GraphPackService {

	Registry registry;
	public RmiInmemoryQuartzGraphPackService(String serviceName,
			Registry registry){
		this(serviceName,registry,null);
	}
	
	public RmiInmemoryQuartzGraphPackService(String serviceName,
								Registry registry, Object[][] extenesions) {
		super(serviceName, ConnectionManager.class,
				  ClientStore.class,
				  NodeStore.class,
				  EdgeStore.class,
				  Parser.class,
				  TaskManager.class,
				  extenesions);

		this.registry = registry;
		
		init();
	}
	
	IParser get(){
		return injector.getInstance(IParser.class);
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
