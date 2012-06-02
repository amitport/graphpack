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
package graphpack.tests;

import static org.junit.Assert.assertEquals;
import graphpack.GraphPackService;
import graphpack.IClient;
import graphpack.INode;
import graphpack.RmiInmemoryQuartzGraphPackService;

import java.rmi.registry.LocateRegistry;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * test basic service persistence
 * @author amitport
 */
public class PersistenceImplTest {

	static GraphPackService s;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		s = new RmiInmemoryQuartzGraphPackService("service",LocateRegistry.createRegistry(1099));
	}

	@Test
	public void graphTest() {
		s.createClient("client1");IClient c1 = s.client("client1");
		
		c1.createNode("a");INode a = c1.node("a");
		assertEquals("service.client1.a",a.toString());
		c1.createNode("b");INode b = c1.node("b");
		
		a.addOutgoingEdge(b.location(),null);
		assertEquals("[a-0->service.client1.b]",a.getOutgoingEdges().toString());

		s.createClient("client2");IClient c2 = s.client("client2");
		c2.createNode("c"); INode c = c2.node("c");
		assertEquals("service.client2.c",c.toString());
		
		a.addOutgoingEdge(c.location(), null);
		assertEquals("[a-0->service.client1.b, a-1->service.client2.c]",a.getOutgoingEdges().toString());
	}

}
