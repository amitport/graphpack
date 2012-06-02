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

import graphpack.Edge;
import graphpack.Edge.Payload;
import graphpack.INodeLocator;
import graphpack.NodeLocation;
import graphpack.local.persistence.IEdgeStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import com.google.inject.Inject;

/** in memory edge store - uses an {@link ArrayList} for storing edges*/
public class EdgeStore implements IEdgeStore {
//	NodeLocation location;
//	List<Edge> edges;
//	long num;
	INodeLocator locator;
	public PersistenceManagerFactory PMfactory;
	
	@Inject
	public EdgeStore(PersistenceManagerFactory PMfactory, INodeLocator locator) {
		this.PMfactory = PMfactory;
		this.locator = locator;
	}
	@Override
	public List<Edge> getOutgoingEdges() {
		PersistenceManager pm = PMfactory.getPersistenceManager();
		List<Edge> $ = new ArrayList<Edge>();
		try {
			@SuppressWarnings("unchecked")
			Set<PersistentEdge> s = pm.getManagedObjects(PersistentEdge.class);
			if (s != null){
				for (PersistentEdge e : s){
					$.add(new Edge(new NodeLocation(e.srcServiceName,e.srcClientName,e.srcNodeName),
							locator.locate(e.srcServiceName,e.srcClientName,new NodeLocation(e.trgServiceName,e.trgClientName,e.trgNodeName)),
							e.num,e.payload));
				}
			}
		} finally {
			pm.close();
		}
		return $;
	}

	@Override
	public void addOutgoingEdge(String sourceService, String sourceClient, String sourceNode, NodeLocation target, Payload payload) {
		PersistenceManager pm = PMfactory.getPersistenceManager();

		PersistentEdge e = new PersistentEdge(sourceService,sourceClient,sourceNode,
				target.getServiceName(),target.getClientName(),target.getNodeName(),payload);
		try {
			pm.makePersistent(e);
		} finally {
			pm.close();
		}
	}

}
