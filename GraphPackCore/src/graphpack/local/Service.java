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
package graphpack.local;

import graphpack.IClient;
import graphpack.IService;
import graphpack.local.persistence.IClientStore;
import graphpack.remote.IConnectionManager;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * a local service
 * delegates persistence to injected {@link graphpack.local.persistence.IClientStore} <br/>
 * delegates connection to injected {@link graphpack.remote.IConnectionManager}
 * @author amitport
 */
public class Service implements IService {

	String serviceName;
	IConnectionManager connectionManager;
	IClientStore clientStore;
	IClientFactory clientFactory;
	@Inject
	public Service(@Named("serviceName") String serviceName,
					IConnectionManager connectionManager,
					IClientStore clientStore,
					IClientFactory clientFactory){
		this.serviceName = serviceName;
		this.connectionManager = connectionManager;
		this.clientStore = clientStore;
		this.clientFactory = clientFactory;
	}
	
	@Override
	public IClient client(String clientName){ return clientStore.get(clientName);}

	@Override
	public void createClient(String clientName) {
		if (clientStore.contains(clientName)){
			throw new RuntimeException("client already exists");
		}
		Client $ = clientFactory.create(serviceName, clientName);
		clientStore.put(clientName, $);
	}
}
