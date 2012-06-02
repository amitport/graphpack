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
package graphpack.remote.rmi;

import graphpack.Edge;
import graphpack.Edge.Payload;
import graphpack.INode;
import graphpack.INodeLocator;
import graphpack.NodeLocation;
import graphpack.SerializableEdge;
import graphpack.matching.Matcher;
import graphpack.matching.ResultSet;
import graphpack.parsing.java.IParser;
import graphpack.taskprocessing.ITask;

import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class ProxyNode implements INode {
	INodeLocator locator;
	String /* targetService, */targetClient, targetNode;
	ProxyService proxyService;
	private IRemotableNode remote;
	IParser parser;
	@Inject
	public ProxyNode(INodeLocator locator,
			IParser parser,
			@Assisted("targetClient") String targetClient,
			@Assisted("targetNode") String targetNode,
			@Assisted ProxyService proxyService) {
		this.locator = locator;
		this.parser = parser;
		this.targetClient = targetClient;
		this.targetNode = targetNode;
		this.proxyService = proxyService;
	}

	public IRemotableNode getRemote() {
		if (remote == null) {
			remote = proxyService.getRemotableNode(targetClient, targetNode);
		}
		return remote;
	}

	@Override
	public NodeLocation location() {
		return new NodeLocation(proxyService.targetService, targetClient,
				targetNode);
	}

	@Override
	public String toString() {
		return proxyService.targetService + "." + targetClient + "."
				+ targetNode;
	}

	@Override
	public List<Edge> getOutgoingEdges() {
		try {
			List<Edge> $ = new ArrayList<Edge>();
			for (SerializableEdge e : getRemote().getOutgoingEdges(
					proxyService.sender)) {
				$.add(new Edge(e.source, locator.locate(
						proxyService.sender.getServiceName(),
						proxyService.sender.getClientName(), e.target), e.num,
						e.payload));
			}
			return $;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void addOutgoingEdge(NodeLocation target, Payload payload) {
		try {
			getRemote().addOutgoingEdge(proxyService.sender, target, payload);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ResultSet traverse(Matcher matcher) {
		try {
			return getRemote().traverse(proxyService.sender, matcher);
		} catch (Throwable e) {
			return ResultSet.Empty();
		}
	}
	
	@Override
	public ResultSet traverse(String path, Object... params) {
		return traverse(parser.parsePath(path, params));
	}
	
	public Object writeReplace() throws ObjectStreamException 
	{return location();}

	@Override
	public void addTask(String taskName, Class<? extends ITask> task) {
		try {
			getRemote().addTask(proxyService.sender, taskName, task);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void addScheduledTask(String taskName,
			Class<? extends ITask> task, String cronExpression){
		try {
			getRemote().addScheduledTask(proxyService.sender, taskName, task, cronExpression);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}		
	}

	@Override
	public void callTask(String taskName, Object... params) {
		try {
			getRemote().callTask(proxyService.sender, taskName, params);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
}
