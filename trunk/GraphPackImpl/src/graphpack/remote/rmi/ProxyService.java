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
import graphpack.IClient;
import graphpack.IService;

import java.rmi.registry.Registry;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class ProxyService implements IService {
	IProxyClientFactory proxyClientFactory;
	Registry registry;
	ClientLocation sender;
	String targetService;
	private IRemotableService remote;
	@Inject
	public ProxyService(IProxyClientFactory proxyClientFactory, Registry registry, @Assisted ClientLocation sender, @Assisted String targetService) {
		this.proxyClientFactory = proxyClientFactory;
		this.registry = registry;
		this.sender = sender;
		this.targetService = targetService;
	}

	public IRemotableService getRemote() {
		try {
			if (remote == null) {
				remote = (IRemotableService) registry.lookup(targetService);
			}
			return remote;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public IClient client(String clientName) {
		return proxyClientFactory.create(clientName,this);
	}
	@Override
	public void createClient(String clientName) {
		try {
			getRemote().createClient(sender, clientName);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String toString() {
		return targetService;
	}

	public IRemotableClient getRemotableClient(String targetClient) {
		try {
			return getRemote().getRemotableClient(sender, targetClient);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public IRemotableNode getRemotableNode(String targetClient,
			String targetNode) {
		try {
			return getRemote().getRemotableClient(sender, targetClient).node(sender, targetNode);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
}
