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
package graphpack.local.persistence.JDO3;

import graphpack.local.Client;
import graphpack.local.IClientFactory;
import graphpack.local.persistence.IClientStore;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import com.google.inject.Inject;

/** in memory client store - uses {@link java.util.HashMap} for storing clients */
public class ClientStore implements IClientStore {

	public PersistenceManagerFactory PMfactory;
	IClientFactory clientFactory;
	
	@Inject
	public ClientStore(PersistenceManagerFactory factory, IClientFactory clientFactory) {
		this.PMfactory = factory;
		this.clientFactory = clientFactory;
	}

	@Override
	public boolean contains(String clientName) {
		return get(clientName)!=null;
	}

	@Override
	public void put(String clientName, Client client) {
		PersistenceManager pm = PMfactory.getPersistenceManager();

		PresistentClient e = new PresistentClient(clientName, client);
		try {
			pm.makePersistent(e);
		} finally {
			pm.close();
		}
	}

	@Override
	public Client get(String clientName) {
		PersistenceManager pm = PMfactory.getPersistenceManager();
		Client c = null;
		try {
			PresistentClient e = pm.getObjectById(PresistentClient.class,
					clientName);
			if (e != null)
				c = clientFactory.create(e.serviceName, e.clientName);
		} finally {
			pm.close();
		}
		return c;
	}
}
