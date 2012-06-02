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

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import graphpack.IClient;
import graphpack.INode;
import graphpack.IService;
import graphpack.local.persistence.INodeStore;
import graphpack.remote.IConnectionManager;

/**
 * a local client <br/>
 * delegates persistence to injected {@link graphpack.local.persistence.INodeStore} <br/>
 * delegates connection to injected {@link graphpack.remote.IConnectionManager}
 * @author amitport
 */
public class Client implements IClient {
	public String serviceName;
	String clientName;
	INodeStore nodeStore;
	INodeFactory nodeFactory;
	IConnectionManager connectionManager;
	@Inject
	public Client(@Assisted("serviceName") String serviceName, @Assisted("clientName") String clientName, INodeStore nodeStore, INodeFactory nodeFactory, IConnectionManager connectionManager) {
		this.serviceName = serviceName;
		this.clientName = clientName;
		this.nodeStore = nodeStore;
		this.nodeFactory = nodeFactory;
		this.connectionManager = connectionManager;
	}
	@Override
	public IService connect(String targetService){
		return connectionManager.connect(serviceName, clientName, targetService);
	}
	@Override
	public void createNode(String nodeName) {
		if (nodeStore.contains(clientName)){
			throw new RuntimeException("client already exists");
		}
		nodeStore.put(nodeName, nodeFactory.create(serviceName,clientName,nodeName));
	}
	@Override
	public INode node(String nodeName) {
		return nodeStore.get(nodeName);
	}
	
	@Override
	public String toString() {
		return serviceName+"."+clientName;
	}
}
