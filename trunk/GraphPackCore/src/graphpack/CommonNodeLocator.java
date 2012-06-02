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

import graphpack.remote.IConnectionManager;

import com.google.inject.Inject;

/**
 * generic implementation of {@link INodeLocator}. locates remote or local nodes
 * @author amitport
 */
public class CommonNodeLocator implements INodeLocator {
	IService local;
	IConnectionManager connectionManager;
	@Inject
	public CommonNodeLocator(IService local, IConnectionManager connectionManager){
		this.local = local;
		this.connectionManager = connectionManager;
	}
	
	@Override
	public INode locate(String sourceService, String sourceClient, NodeLocation location){
		if (location.getServiceName().equals(sourceService)) {
			return local.client(location.clientName).node(location.nodeName);
		} else {
			return connectionManager.connect(sourceService, sourceClient, location.serviceName)
					.client(location.clientName).node(location.nodeName);
		}
	}
}
