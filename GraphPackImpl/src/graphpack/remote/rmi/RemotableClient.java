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

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RemotableClient implements IRemotableClient {
	Gateway.Client gateway;
	Map<String,RemotableNode> alreadyExported;
	public RemotableClient(Gateway.Client gateway){
		this.gateway = gateway;
		try {
			UnicastRemoteObject.exportObject(this, 0);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
		alreadyExported = new HashMap<String, RemotableNode>();
	}
	
	@Override
	public IService connect(ClientLocation sender,
			String targetService) throws RemoteException {
		return gateway.connect(sender,targetService);
	}
	
	public void unexport(){
		try {
			UnicastRemoteObject.unexportObject(this, false);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
		for (Iterator<RemotableNode> iterator = alreadyExported.values().iterator(); iterator.hasNext();) {
			RemotableNode type = iterator.next();
			type.unexport();
		}
	}

	@Override
	public void createNode(ClientLocation sender,
			String nodeName) {
		gateway.createNode(sender,nodeName);
	}

	@Override
	public IRemotableNode node(ClientLocation sender,
			String nodeName) throws RemoteException {
		if (alreadyExported.containsKey(nodeName)){
			return alreadyExported.get(nodeName);
		} else {
			RemotableNode $ = new RemotableNode(gateway.node(sender,nodeName));
			alreadyExported.put(nodeName, $);
			return $;
		}
	}



}
