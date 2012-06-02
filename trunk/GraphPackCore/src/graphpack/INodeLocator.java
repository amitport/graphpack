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
 * locates an {@link INode} </br>
 * @author amitport
 */
public interface INodeLocator {
	/**
	 * locates the {@link INode} </br> 
	 * @param sourceService the service to which the requesting client belongs to
	 * @param sourceClient the client that wants to locate some node
	 * @param location the location of the node that should be located
	 * @return the node (which may be local or a proxy of a remote node)
	 */
	INode locate(String sourceService, String sourceClient, NodeLocation location);
}
