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
package graphpack.local.persistence;

import graphpack.local.Client;

/**
 * {@link graphpack.IClient} abstract persistence implementor
 * @author amitport
 */
public interface IClientStore {
	/**
	 * check if a certain client exist in the store
	 * @param clientName the name of the client
	 * @return true IFF the client exists
	 */
	boolean contains(String clientName);
	
	/**
	 * store a new client
	 * @param clientName the new client name
	 * @param client the new client object
	 */
	void put(String clientName,Client client);
	
	/**
	 * get an existing client
	 * @param clientName the client name
	 * @return a reference to the client
	 */
	Client get(String clientName);
}
