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

/**
 * a generic reference to a service - it can be either local or remote
 * @author amitport
 */
public interface IService {
	
	/**
	 * creates a new client in this service
	 * @param clientName the name of the new client
	 */
	void createClient(String clientName);
	
	/**
	 * return a reference to a client in this service
	 * @param clientName the name of the requested client
	 * @return the client
	 */
	IClient client(String clientName);	
}
