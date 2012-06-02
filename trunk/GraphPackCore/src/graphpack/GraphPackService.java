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

import java.util.HashMap;
import java.util.Map;

import graphpack.extensions.Extensions;
import graphpack.extensions.Extension;
import graphpack.local.Client;
import graphpack.local.IClientFactory;
import graphpack.local.INodeFactory;
import graphpack.local.Node;
import graphpack.local.Service;
import graphpack.local.persistence.IClientStore;
import graphpack.local.persistence.IEdgeStore;
import graphpack.local.persistence.INodeStore;
import graphpack.parsing.java.IParser;
import graphpack.remote.IConnectionManager;
import graphpack.taskprocessing.ITaskManager;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import com.google.inject.util.Providers;

/**
 * An abstraction of a GraphPack service (<a href="http://en.wikipedia.org/wiki/Bridge_pattern">bridge design pattern</a>) 
 * @author amitport
 */
public class GraphPackService extends AbstractModule implements IService {
	String serviceName;
	Class<? extends IConnectionManager> connectionManagerClass;
	Class<? extends IClientStore> clientStoreClass;
	Class<? extends INodeStore> nodeStoreClass;
	Class<? extends IEdgeStore> edgeStoreClass;
	Class<? extends IParser> parserClass;
	Class<? extends ITaskManager> taskManager;
	Extensions extensions;
	
	IService local;
	IConnectionManager connectionManager;
	
	Injector injector;
	
	public GraphPackService(String serviceName,
			Class<? extends IConnectionManager> connectionMangerClass,
			Class<? extends IClientStore> clientStoreClass,
			Class<? extends INodeStore> nodeStoreClass,
			Class<? extends IEdgeStore> edgeStoreClass,
			Class<? extends IParser> parserClass,
			Class<? extends ITaskManager> taskManager,
			Object[][] extensions) {
		this.serviceName = serviceName;
		this.connectionManagerClass = connectionMangerClass;
		this.clientStoreClass = clientStoreClass;
		this.nodeStoreClass = nodeStoreClass;
		this.edgeStoreClass = edgeStoreClass;
		this.parserClass = parserClass;
		this.taskManager = taskManager;
		
		Map<String,Extension> map = new HashMap<String,Extension>();
		if (extensions != null){
			for (Object[] pair : extensions){
				map.put((String)pair[0], (Extension)pair[1]);
			}
		}
		this.extensions = new Extensions(map);
	}
	
	protected void init(){
		injector = Guice.createInjector(this);
		
		local = injector.getInstance(IService.class);
		connectionManager = injector.getInstance(IConnectionManager.class);
	}
	
	@Override 
	protected void configure() {
		bind(String.class).annotatedWith(Names.named("serviceName")).toInstance(serviceName);
		bind(IService.class).to(Service.class).in(Singleton.class);
		bind(Service.class).in(Singleton.class);
		bind(IConnectionManager.class).to(connectionManagerClass).in(Singleton.class);
		bind(INodeLocator.class).to(CommonNodeLocator.class).in(Singleton.class);
		bind(IParser.class).to(parserClass).in(Singleton.class);
		bind(ITaskManager.class).to(taskManager).in(Singleton.class);
		bind(Extensions.class).toProvider(Providers.of(extensions));
		
		bind(IClientStore.class).to(clientStoreClass);
		bind(INodeStore.class).to(nodeStoreClass);
		bind(IEdgeStore.class).to(edgeStoreClass);
		
		install(new FactoryModuleBuilder()
			.implement(INode.class,Node.class)
			.build(INodeFactory.class));
		install(new FactoryModuleBuilder()
				.implement(IClient.class,Client.class)
				.build(IClientFactory.class));
	}
	
	/**
	 * start this GraphPack service and make it available remotely
	 */
	public void start(){
		connectionManager.export(serviceName,local);
	}
	
	/**
	 * closes this GraphPack service
	 */
	public void shutdown(){
		connectionManager.unexport();
	}

	@Override
	public void createClient(String clientName) {
		local.createClient(clientName);
	}

	@Override
	public IClient client(String clientName) {
		return local.client(clientName);
	}
}
