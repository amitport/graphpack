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
package graphpack.remote.rmi;

import graphpack.ClientLocation;
import graphpack.IService;
import graphpack.remote.Gateway;
import graphpack.remote.IConnectionManager;

import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import com.google.inject.Inject;

/** 
 * an RMI connection manager
 * @author amitport
 */
public class ConnectionManager implements IConnectionManager {		
	IProxyServiceFactory proxyServiceFactory;
	RemotableService remote;
	Registry registry;
	
	@Inject
	public ConnectionManager(IProxyServiceFactory proxyServiceFactory, Registry registry) {
		this.proxyServiceFactory = proxyServiceFactory;
		this.registry = registry;
	}
	
	@Override
	public IService connect(String sourceService, String sourceClient, String targetService) {
		return proxyServiceFactory.create(new ClientLocation(sourceService, sourceClient), targetService);
	}
	
	@Override
	public void export(String serviceName, IService service){
		try {
			remote = new RemotableService(new Gateway.Service(service));
			registry.rebind(serviceName, UnicastRemoteObject.exportObject(remote, 0));
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void unexport(){
		remote.unexport();
	}

}
