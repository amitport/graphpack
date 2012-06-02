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

import graphpack.local.INodeFactory;
import graphpack.local.Node;
import graphpack.local.persistence.INodeStore;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import com.google.inject.Inject;

/** in memory node store - uses {@link java.util.HashMap} for storing nodes */
public class NodeStore implements INodeStore {

	public PersistenceManagerFactory PMfactory;
	INodeFactory nodeFactory;
	
	@Inject
	public NodeStore(PersistenceManagerFactory factory, INodeFactory nodeFactory) {
		this.PMfactory = factory;
		this.nodeFactory = nodeFactory;
	}
	
	@Override
	public boolean contains(String nodeName) {
		return get(nodeName)!=null;
	}

	@Override
	public void put(String nodeName, Node node) {
		PersistenceManager pm = PMfactory.getPersistenceManager();

		PresistentNode e = new PresistentNode(nodeName, node);
		try {
			pm.makePersistent(e);
		} finally {
			pm.close();
		}
	}

	@Override
	public Node get(String nodeName) {
		PersistenceManager pm = PMfactory.getPersistenceManager();
		Node n = null;
		try {
			PresistentNode e = pm.getObjectById(PresistentNode.class,
					nodeName);
			if (e != null)
				n = nodeFactory.create(e.serviceName, e.clientName, e.nodeName);
		} finally {
			pm.close();
		}
		return n;
	}}
