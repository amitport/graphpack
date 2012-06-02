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
package graphpack.local.persistence.inmemory;

import graphpack.Edge;
import graphpack.Edge.Payload;
import graphpack.INodeLocator;
import graphpack.NodeLocation;
import graphpack.local.persistence.IEdgeStore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.inject.Inject;

/** in memory edge store - uses an {@link ArrayList} for storing edges*/
public class EdgeStore implements IEdgeStore {
	NodeLocation location;
	List<Edge> edges;
	long num;
	INodeLocator locator;
	
	@Inject
	public EdgeStore(INodeLocator locator) {
		this.locator = locator;
		edges = new ArrayList<Edge>();
		num = 0;
	}
	@Override
	public List<Edge> getOutgoingEdges() {
		return Collections.unmodifiableList(edges);
	}

	@Override
	public void addOutgoingEdge(String sourceService, String sourceClient, String sourceNode, NodeLocation target, Payload payload) {
		edges.add(new Edge(new NodeLocation(sourceService,sourceClient,sourceNode),
				locator.locate(sourceService, sourceClient, target),
				num++,payload));
	}

}
