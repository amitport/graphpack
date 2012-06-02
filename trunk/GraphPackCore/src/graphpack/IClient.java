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
 * a generic reference to a client - it can be either local or remote
 * @author amitport
 */
public interface IClient {
	
	/**
	 * connect to a remote service using this client credentials
	 * @param targetService the service to connect
	 * @return a reference to the remote service
	 */
	IService connect(String targetService);
	
	/**
	 * creates a new node in this client's graph
	 * @param nodeName the name of the new node
	 */
	void createNode(String nodeName);
	
	/**
	 * return a reference to a node in this client's graph
	 * @param nodeName the name of the requested node
	 * @return the node
	 */
	INode node(String nodeName);	
	
}
