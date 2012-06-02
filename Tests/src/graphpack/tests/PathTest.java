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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import graphpack.Edge.Payload;
import graphpack.GraphPackService;
import graphpack.RmiInmemoryQuartzGraphPackService;
import graphpack.IClient;
import graphpack.INode;
import graphpack.matching.Result;
import graphpack.matching.ResultSet;
import graphpack.parsing.java.IParser;
import graphpack.parsing.scala.Parser;

import java.rmi.registry.LocateRegistry;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * test PackCypher path parser and matcher
 * @author amitport
 */
public class PathTest {
	static INode n1,n2,n3,n4,n5,n6,n7;
	static IParser parser;
	
	public static class Prop implements Payload {
		private static final long serialVersionUID = 8814957448790227355L;
		
		public int priority;
		public Prop(int priority){
			this.priority = priority;
		}
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		parser = new Parser();
		GraphPackService b = new RmiInmemoryQuartzGraphPackService("s", LocateRegistry.createRegistry(1099));		
		b.createClient("c"); IClient c = b.client("c");
		c.createNode("n1"); n1 = c.node("n1");
		c.createNode("n2"); n2 = c.node("n2");
		c.createNode("n3"); n3 = c.node("n3");
		c.createNode("n4"); n4 = c.node("n4");
		c.createNode("n5"); n5 = c.node("n5");
		c.createNode("n6"); n6 = c.node("n6");
		c.createNode("n7"); n7 = c.node("n7");
		
		n1.addOutgoingEdge(n2.location(), new Prop(5));
		n1.addOutgoingEdge(n3.location(), new Prop(7));
		n1.addOutgoingEdge(n4.location(), Payload.EMPTY);

		n2.addOutgoingEdge(n5.location(), Payload.EMPTY);
		n2.addOutgoingEdge(n6.location(), Payload.EMPTY);
		
		n3.addOutgoingEdge(n6.location(), Payload.EMPTY);

		n5.addOutgoingEdge(n2.location(), Payload.EMPTY);

		n5.addOutgoingEdge(n7.location(), Payload.EMPTY);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void pathTest1() throws Exception {
		ResultSet rs = n1.traverse(parser.parsePath("-->x"));
		Result res; 
		res = new Result();
		res.put("x",n2.location());
		rs.contains(res);
		assertTrue(rs.elementSet().contains(res));
		res = new Result();
		res.put("x",n3.location());
		assertTrue(rs.contains(res));
		res = new Result();
		res.put("x",n4.location());
		
		assertTrue(rs.contains(res));
	}
	@Test
	public void pathTest2() throws Exception {
		ResultSet rs;
		Result res; 
		rs = n1.traverse(parser.parsePath("-->x?" +
				"(null!=_edg.priority and _edg.priority>6)"));
		res = new Result();
		res.put("x",n3.location());
		assertTrue(rs.contains(res));
		rs = n1.traverse(parser.parsePath("-[y]->x?" +
				"(null!=y.priority and y.priority>6)"));
		res = new Result();
		res.put("x",n3.location());
		res.put("y",n1.getOutgoingEdges().get(1).serializableEdge);
		assertTrue(rs.contains(res));
		rs = n1.traverse(parser.parsePath("-[y]->?" +
				"   (null!=y.priority and y.priority>6)"));
		res = new Result();
		res.put("y",n1.getOutgoingEdges().get(1).serializableEdge);
		assertTrue(rs.contains(res));
	}
	
	@Test
	public void pathTest3() throws Exception {
		ResultSet rs;
		Result res; 
		rs = n1.traverse(parser.parsePath("-->-->x"));
		res = new Result();
		res.put("x",n5.location());
		assertTrue(rs.contains(res));
		res = new Result();
		res.put("x",n6.location());
		assertTrue(rs.contains(res));
		
		rs = n1.traverse(parser.parsePath("-->y-->x-->y"));
		res = new Result();
		res.put("x",n5.location());
		res.put("y",n2.location());
		assertTrue(rs.contains(res));
		res = new Result();
		res.put("x",n6.location());
		res.put("y",n2.location());
		assertFalse(rs.contains(res));
	}
	@Test
	public void pathTest4() throws Exception {
		ResultSet rs;
		Result res; 
		rs = n1.traverse(parser.parsePath("-->*0..1-->x"));
		res = new Result();
		res.put("x",n2.location());
		assertTrue(rs.contains(res));
		res.put("x",n3.location());
		assertTrue(rs.contains(res));
		res.put("x",n4.location());
		assertTrue(rs.contains(res));
		res.put("x",n5.location());
		assertTrue(rs.contains(res));
		res.put("x",n6.location());
		assertTrue(rs.contains(res));
		res.put("x",n7.location());
		assertFalse(rs.contains(res));
	}
	@Test
	public void pathTest5() throws Exception {
		ResultSet rs;
		Result res; 
		rs = n1.traverse(parser.parsePath("-->*0..1-->x"));
		rs = n1.traverse(parser.parsePath("(-->*0..1-->x)"));
		rs = n1.traverse(parser.parsePath("((-->-->)*0..1-->x)*0..1"));
		res = new Result();
		res.put("x",n2.location());
		assertTrue(rs.contains(res));
		res.put("x",n3.location());
		assertTrue(rs.contains(res));
		res.put("x",n4.location());
		assertTrue(rs.contains(res));
		res.put("x",n7.location());
		assertTrue(rs.contains(res));
	}
}
