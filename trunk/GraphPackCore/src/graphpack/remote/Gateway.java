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
package graphpack.remote;

import java.util.List;

import graphpack.ClientLocation;
import graphpack.Edge;
import graphpack.Edge.Payload;
import graphpack.IClient;
import graphpack.INode;
import graphpack.IService;
import graphpack.NodeLocation;
import graphpack.matching.Matcher;
import graphpack.matching.ResultSet;
import graphpack.taskprocessing.ITask;

/**
 * this class handles any incoming function calls
 * @author amitport
 */
public class Gateway {
	//TODO authorization
	
	/**
	 * handles incoming {@link graphpack.IService} function calls
	 * @author amitport
	 */
	public static class Service {
		IService service;
		public Service(IService service){
			this.service = service;
		}
		public Client client(ClientLocation sender, String clientName){
			return new Client(service.client(clientName));
		}
		public void createClient(ClientLocation sender,
				String clientName) {
			service.createClient(clientName);
		}
	}

	/**
	 * handles incoming {@link graphpack.IClient} function calls
	 * @author amitport
	 */	
	public static class Client {
		IClient client;
		public Client(IClient client) {
			this.client = client;
		}
		public IService connect(ClientLocation sender,
				String targetService) {
			return client.connect(targetService);
		}
		public void createNode(ClientLocation sender,
				String nodeName) {
			client.createNode(nodeName);
		}
		public Node node(ClientLocation sender,
				String nodeName) {
			return new Node(client.node(nodeName));
		}
	}
	
	/**
	 * handles incoming {@link graphpack.INode} function calls
	 * @author amitport
	 */	
	public static class Node {
		INode node;
		public Node(INode node) {
			this.node = node;
		}
		
		public List<? extends Edge> getOutgoingEdges(ClientLocation sender) {
			return node.getOutgoingEdges();
		}

		public void addOutgoingEdge(ClientLocation sender,
				NodeLocation target, Payload payload) {
			node.addOutgoingEdge(target, payload);
		}

		public ResultSet traverse(ClientLocation sender, Matcher matcher) {
			return node.traverse(matcher);
		}

		public void addTask(ClientLocation sender, String taskName,
				Class<? extends ITask> task) {
			node.addTask(taskName, task);
		}

		public void addScheduledTask(ClientLocation sender, String taskName,
				Class<? extends ITask> task, String cronExpression) {
			node.addScheduledTask(taskName, task, cronExpression);
		}
		
		public void callTask(ClientLocation sender, String taskName,
				Object... params) {
			node.callTask(taskName, params);
		}

	}
}
