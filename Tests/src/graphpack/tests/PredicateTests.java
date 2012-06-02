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
import graphpack.parsing.scala.Parser;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * test PackCypher predicate parser and matcher
 * @author amitport
 */
public class PredicateTests {

	static Parser parser;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		parser = new Parser();
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

	public void predTest(String pred,boolean expectedResults){
		predTest(pred,expectedResults,null);
	}
	public void predTest(String pred,boolean expectedResults,Map<String,Object> env){
		assertEquals(expectedResults,parser.parsePredicate(pred).evaluate(env));
	}
	@Test
	public void predicateParseComparisonTest() {
		predTest("4>2",true);
		predTest("2>2",false);
		predTest("2>4",false);

		predTest("4>=2",true);
		predTest("2>=2",true);
		predTest("2>=4",false);
		
		predTest("4<2",false);
		predTest("2<2",false);
		predTest("2<4",true);
		
		predTest("4<=2",false);
		predTest("2<=2",true);
		predTest("2<=4",true);
	}
	
	@Test
	public void predicateParseEqualsTest() {		
		predTest("2==2",true);
		predTest("2.0==2",true);
		predTest("2.0==2.0",true);
		predTest("3==2",false);
		predTest("3.2==2",false);
		predTest("3.2==2.3",false);
		
		predTest("2!=2",false);
		predTest("2.0!=2",false);
		predTest("2.0!=2.0",false);
		predTest("3!=2",true);
		predTest("3.2!=2",true);
		predTest("3.2!=2.3",true);
	}
	
	@Test
	public void predicateParseBooleanTest() {
		predTest("0==0 and 0==0",true);
		predTest("0==0 and 0==1",false);
		predTest("0==1 and 0==0",false);
		predTest("0==1 and 0==1",false);
				
		predTest("0==0 or 0==0",true);
		predTest("0==0 or 0==1",true);
		predTest("0==1 or 0==0",true);
		predTest("0==1 or 0==1",false);				
	}
	
	
	public static class Props {
		public boolean a = true;
		public int b = 4;
		public double c = 4.5;
		public String d = "test";		
	}
	
	@Test
	public void predicateParseEnvTest() {
		Map<String,Object> env = new HashMap<String,Object>();
		Object props = new Props();
		env.put("x", props);
		
		predTest("x.a==true",true,env);
		predTest("x.b==4",true,env);
		predTest("x.c>x.b",true,env);
		predTest("'test'==x.d",true,env);
	}
}
