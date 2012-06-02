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

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import graphpack.IClient;
import graphpack.INode;
import graphpack.IService;

public class ProxyClient implements IClient {
	IProxyNodeFactory proxyNodeFactory;
	String targetClient;
	private IRemotableClient remote;
	ProxyService proxyService;
	@Inject
	public ProxyClient(IProxyNodeFactory proxyNodeFactory, @Assisted String targetClient, @Assisted ProxyService proxyService) {
		this.proxyNodeFactory = proxyNodeFactory;
		this.targetClient = targetClient;
		this.proxyService = proxyService;
	}
	
	public IRemotableClient getRemote() {
		if (remote == null) {
			remote = proxyService.getRemotableClient(targetClient);
		}
		return remote;
	}
	
	@Override
	public IService connect(String targetService) {
		try {
			return getRemote().connect(proxyService.sender,targetService);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void createNode(String nodeName) {
		try{
			getRemote().createNode(proxyService.sender,nodeName);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public INode node(String nodeName) {
		return proxyNodeFactory.create(targetClient, nodeName, proxyService);
	}

	@Override
	public String toString() {
		return proxyService.targetService+"."+targetClient;
	}
}
