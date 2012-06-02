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
import graphpack.remote.Gateway;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RemotableService implements IRemotableService {

	Gateway.Service gateway;
	Map<String,RemotableClient> alreadyExported;
	public RemotableService(Gateway.Service gateway){
		this.gateway = gateway;
		alreadyExported = new HashMap<String, RemotableClient>();
	}
	
	@Override
	public IRemotableClient getRemotableClient(ClientLocation sender, String clientName) throws RemoteException {
		if (alreadyExported.containsKey(clientName)){
			return alreadyExported.get(clientName);
		} else {
			RemotableClient $ = new RemotableClient(gateway.client(sender,clientName));
			alreadyExported.put(clientName, $);
			return $;
		}
	}
	
	public void unexport(){
		try {
			UnicastRemoteObject.unexportObject(this, false);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
		for (Iterator<RemotableClient> iterator = alreadyExported.values().iterator(); iterator.hasNext();) {
			RemotableClient type = iterator.next();
			type.unexport();
		}
	}

	@Override
	public void createClient(ClientLocation sender, String clientName) {
		gateway.createClient(sender,clientName);
	}

}
