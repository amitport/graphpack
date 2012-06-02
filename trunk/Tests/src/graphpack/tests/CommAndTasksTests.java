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
import graphpack.Edge.Payload;
import graphpack.GraphPackService;
import graphpack.IClient;
import graphpack.INode;
import graphpack.NodeLocation;
import graphpack.RmiGraphPackClient;
import graphpack.RmiInmemoryQuartzGraphPackService;
import graphpack.extensions.Extension;
import graphpack.taskprocessing.ITask;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * test communication and task management
 * @author amitport
 */
public class CommAndTasksTests {
	static INode n1,n2;
	static GraphPackService s;
	static IClient c;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("org.quartz.scheduler.skipUpdateCheck", "true");
		
		Registry reg = LocateRegistry.createRegistry(1099);
		s = new RmiInmemoryQuartzGraphPackService("s", reg,
				new Object[][] {
				{"test",new Extension(){@Override public Object extendNode(INode node) {return "exxxxXxxxx";}}}
				});	
		s.createClient("c");
		s.client("c").createNode("n1"); n1 = s.client("c").node("n1");
		s.client("c").createNode("n2"); n2 = s.client("c").node("n2");
		
		n1.addOutgoingEdge(n2.location(), Payload.EMPTY);
		
		s.start();
		
		c = new RmiGraphPackClient(reg).connectAs("remote_s", "c2").toService("s").client("c");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		s.shutdown();
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test1() {
		assertEquals("[n1-0->s.c.n2]",
				c.node("n1").getOutgoingEdges().toString());
	}
	@Test
	public void test2() {		
		assertEquals("[{x=s.c.n2}]",c.node("n1").traverse("-->x").toString());
	}
	
	@Test
	public void test3() {		
		c.node("n1").addOutgoingEdge(new NodeLocation("ss","cc","n4"), Payload.EMPTY);

		assertEquals("[{x=ss.cc.n4}, {x=s.c.n2}]",c.node("n1").traverse("-->x").toString());
	}
	
	@Test
	public void test4() {		
		assertEquals("[{x=n1-1->ss.cc.n4}, {x=n1-0->s.c.n2}]",c.node("n1").traverse("-[x]->").toString());
	}
	
	@Test
	public void test5() {
		n1.addTask("task", T.class);
		c.node("n1").callTask("task","yey!");
	}
	
	public static class T implements ITask {
		@Override
		public void execute(INode node, Map<String,Object> e, Object... params) {
			System.out.println(e.get("test")+"  " +node.getOutgoingEdges() + " yep task is running... " + params[0]);
		}
	}

}
