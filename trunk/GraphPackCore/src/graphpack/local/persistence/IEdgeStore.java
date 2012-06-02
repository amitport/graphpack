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

import graphpack.Edge;
import graphpack.Edge.Payload;
import graphpack.NodeLocation;

import java.util.List;

/**
 * {@link graphpack.Edge} abstract persistence implementor
 * @author amitport
 */
public interface IEdgeStore {

	/**
	 * @return get all outgoing edges
	 */
	List<Edge> getOutgoingEdges();

	/**
	 * create and add an outgoing edge to the store
	 * @param sourceService the name of the source service
	 * @param sourceClient the name of the source client
	 * @param sourceNode the name of the source node
	 * @param target the location of the target node
	 * @param payload the payload of the edge
	 */
	void addOutgoingEdge(String sourceService, String sourceClient, String sourceNode, NodeLocation target, Payload payload);

}
